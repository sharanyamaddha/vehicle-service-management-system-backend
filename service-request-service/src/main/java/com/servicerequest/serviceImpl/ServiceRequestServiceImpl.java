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

        Map<String, Object> data = new HashMap<>();
        data.put("requestNumber", sr.getRequestNumber());
        data.put("vehicleId", sr.getVehicleId());
        data.put("issue", sr.getIssue());
        data.put("priority", sr.getPriority());
        data.put("status", sr.getStatus());

        publishNotification("SERVICE_BOOKED", customerEmail, data);


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

        Map<String, Object> data = new HashMap<>();
        data.put("requestNumber", sr.getRequestNumber());
        data.put("vehicleId", sr.getVehicleId());
        data.put("technicianId", dto.getTechnicianId());
        data.put("bayNumber", dto.getBayNumber());
        data.put("status", ServiceStatus.ASSIGNED);

        publishNotification("SERVICE_ASSIGNED", customerEmail, data);

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

        if(sr.getStatus() != ServiceStatus.IN_PROGRESS)
            throw new RuntimeException("Service must be in progress to mark completed");

        if(dto.getStatus() != ServiceStatus.COMPLETED)
            throw new RuntimeException("Only COMPLETED status allowed here");

        sr.setStatus(ServiceStatus.COMPLETED);
        serviceReqRepo.save(sr);

        return "Service marked as completed";
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
    public String startJob(String id){

        ServiceRequest sr = serviceReqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if(sr.getStatus() != ServiceStatus.ASSIGNED)
            throw new RuntimeException("Job can be started only after assignment");

        sr.setStatus(ServiceStatus.IN_PROGRESS);
        serviceReqRepo.save(sr);

        return "Job started successfully";
    }

    @Override
    public String closeRequest(String id){

        ServiceRequest sr = serviceReqRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if(sr.getStatus() != ServiceStatus.COMPLETED)
            throw new RuntimeException("Service must be completed before closing");

        sr.setStatus(ServiceStatus.CLOSED);
        serviceReqRepo.save(sr);

        // existing CLOSE logic
        inventoryClient.deductStock(sr.getUsedParts());
        billingService.generateInvoice(sr.getId());
        bayService.releaseBay(sr.getBayNumber());
        userClient.decrement(sr.getTechnicianId());

        return "Service closed successfully";
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
    
    private void publishNotification(String type, String email, Map<String, Object> data) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("email", email);
        payload.put("data", data);

        rabbitTemplate.convertAndSend(
                NotificationConstants.EXCHANGE,
                NotificationConstants.ROUTING_KEY,
                payload
        );
    }





}
