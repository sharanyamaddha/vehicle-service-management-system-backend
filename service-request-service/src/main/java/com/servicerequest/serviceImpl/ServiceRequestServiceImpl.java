package com.servicerequest.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.servicerequest.FeignClient.InventoryClient;
import com.servicerequest.FeignClient.UserClient;
import com.servicerequest.enums.PartsStatus;
import com.servicerequest.enums.ServiceStatus;
import com.servicerequest.event.NotificationEvent;
import com.servicerequest.exceptions.RequestAlreadyAssignedException;
import com.servicerequest.messaging.NotificationConstants;
import com.servicerequest.model.ServiceBay;
import com.servicerequest.model.ServiceRequest;
import com.servicerequest.model.UsedPart;
import com.servicerequest.repository.ServiceRequestRepository;
import com.servicerequest.requestdto.AssignTechnicianDTO;
import com.servicerequest.requestdto.ServiceRequestDTO;
import com.servicerequest.requestdto.UpdateStatusDTO;
import com.servicerequest.responsedto.ServiceRequestResponse;
import com.servicerequest.service.BillingService;
import com.servicerequest.service.ServiceBayService;
import com.servicerequest.service.ServiceRequestService;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

	@Autowired
	private ServiceRequestRepository serviceReqRepo;
	
	@Autowired
	private ServiceBayService bayService;
	
	@Autowired
	private InventoryClient inventoryClient;
	
	@Autowired
	private BillingService billingService;
	
	@Autowired
	private UserClient userClient;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	
    @Override
    public ServiceRequestResponse createRequest(ServiceRequestDTO dto) {

        ServiceRequest sr = new ServiceRequest();
        sr.setRequestNumber("SR-" + UUID.randomUUID().toString().substring(0,6));
        sr.setVehicleId(dto.getVehicleId());
        sr.setCustomerId(dto.getCustomerId());
        sr.setPriority(dto.getPriority());
        sr.setIssue(dto.getIssue());

        ServiceRequest saved = serviceReqRepo.save(sr);

        String customerEmail = userClient.getUser(sr.getCustomerId()).getEmail();

        publishEvent("SERVICE_BOOKED",
        	    "Your service request has been booked successfully",
        	    customerEmail);

        return new ServiceRequestResponse(saved.getId(),
                                          saved.getRequestNumber(),
                                          saved.getStatus());
    }
    
    
    @Override
    public List<ServiceRequest> getCustomerRequests(String customerId) {
        return serviceReqRepo.findByCustomerId(customerId);
    }
    
    
    @Override
    public String assignTechnician(String id,AssignTechnicianDTO dto) {
    	
    	ServiceRequest sr=serviceReqRepo.findById(id)
    	        .orElseThrow(() -> new RuntimeException("Request not found"));
    	
        if(sr.getStatus() != ServiceStatus.REQUESTED)
            throw new RequestAlreadyAssignedException(
                "This service request was already assigned by another manager."
            );
        
        String customerEmail = userClient.getUser(sr.getCustomerId()).getEmail();

        publishEvent("SERVICE_ASSIGNED",
        	    "Your vehicle has been assigned to technician",
        	    customerEmail);

        ServiceBay bay=bayService.findByBayNumber(dto.getBayNumber());
        
        if (!bay.isAvailable())
            throw new RuntimeException("This bay is already occupied.");
        
        bayService.occupyBay(bay.getBayNumber());
    	
    	sr.setTechnicianId(dto.getTechnicianId());
    	sr.setBayNumber(dto.getBayNumber());
    	sr.setStatus(ServiceStatus.ASSIGNED);
    	
    	try {
    		serviceReqRepo.save(sr);
    		userClient.increment(dto.getTechnicianId());

    	}catch(OptimisticLockingFailureException e) {
    		
    		bayService.releaseBay(bay.getBayNumber());
    		
            throw new RequestAlreadyAssignedException(
                    "This service request was already assigned by another manager."
                );    	
        }

    	return "Assigned successfully";

    }
    
    @Override
    public String updateStatus(String id, UpdateStatusDTO dto) {

        ServiceRequest sr = serviceReqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        ServiceStatus current = sr.getStatus();
        ServiceStatus next = dto.getStatus();

        // Guard must be BEFORE status change
        if(current == ServiceStatus.CLOSED)
            throw new RuntimeException("Service already closed");

        if(current == ServiceStatus.ASSIGNED && next != ServiceStatus.IN_PROGRESS)
            throw new RuntimeException("Technician must start work first");

        if(current == ServiceStatus.IN_PROGRESS && next != ServiceStatus.COMPLETED)
            throw new RuntimeException("Technician must complete before closing");

        if(current == ServiceStatus.COMPLETED && next != ServiceStatus.CLOSED)
            throw new RuntimeException("Manager must close the service");

        sr.setStatus(next);
        serviceReqRepo.save(sr);

        if(next == ServiceStatus.CLOSED){
            inventoryClient.deductStock(sr.getUsedParts());

            billingService.generateInvoice(sr.getId());
            bayService.releaseBay(sr.getBayNumber());
            userClient.decrement(sr.getTechnicianId());
        }

        return "Status updated successfully";
    }


    
    @Override
    public String requestParts(String requestId, List<UsedPart> parts) {

        ServiceRequest sr = serviceReqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if(sr.getStatus() != ServiceStatus.IN_PROGRESS)
            throw new RuntimeException("Parts can be requested only when work is in progress");

        if(sr.getPartsStatus() == PartsStatus.PARTS_APPROVED)
            throw new RuntimeException("Parts already approved");

        sr.setUsedParts(parts);
        sr.setPartsStatus(PartsStatus.PARTS_REQUESTED);
        sr.setPartsRequestedBy(sr.getTechnicianId());
        sr.setPartsRequestedAt(LocalDateTime.now());

        serviceReqRepo.save(sr);
        return "Parts requested successfully";
    }
	
    
    @Override
    public String approveParts(String requestId, String managerId) {

        ServiceRequest sr = serviceReqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if(sr.getPartsStatus() != PartsStatus.PARTS_REQUESTED)
            throw new RuntimeException("No pending parts request");

        sr.setPartsStatus(PartsStatus.PARTS_APPROVED);
        sr.setPartsIssuedBy(managerId);
        sr.setPartsIssuedAt(LocalDateTime.now());

        serviceReqRepo.save(sr);
        return "Parts approved successfully";
    }



    
    @Override
    public List<ServiceRequest> getByStatus(ServiceStatus status){
    	return serviceReqRepo.findByStatus(status);
    }
    
    
    @Override
    public List<ServiceRequest> getTechnicianRequests(String techId){
        return serviceReqRepo.findByTechnicianId(techId);
    }

    @Override
    public List<ServiceRequest> getAllRequests(){
        return serviceReqRepo.findAll();
    }

    @Override
    public ServiceRequest getById(String id){
        return serviceReqRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Request not found"));
    }
    
    private void publishEvent(String type, String message, String email) {

        NotificationEvent event = new NotificationEvent();
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("message", message);
        payload.put("email", email);


        rabbitTemplate.convertAndSend(
                NotificationConstants.EXCHANGE,
                NotificationConstants.ROUTING_KEY,
                event
        );
    }



}
