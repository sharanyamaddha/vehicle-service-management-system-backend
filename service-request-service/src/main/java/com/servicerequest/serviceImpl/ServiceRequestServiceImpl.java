package com.servicerequest.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.servicerequest.enums.ServiceStatus;
import com.servicerequest.exceptions.RequestAlreadyAssignedException;
import com.servicerequest.model.PartsStatus;
import com.servicerequest.model.ServiceBay;
import com.servicerequest.model.ServiceRequest;
import com.servicerequest.model.UsedPart;
import com.servicerequest.repository.ServiceRequestRepository;
import com.servicerequest.requestdto.AssignTechnicianDTO;
import com.servicerequest.requestdto.ServiceRequestCreateDTO;
import com.servicerequest.requestdto.UpdateStatusDTO;
import com.servicerequest.responsedto.ServiceRequestResponse;
import com.servicerequest.service.ServiceBayService;
import com.servicerequest.service.ServiceRequestService;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

	@Autowired
	private ServiceRequestRepository serviceReqRepo;
	
	@Autowired
	private ServiceBayService bayService;
	
    @Override
    public ServiceRequestResponse createRequest(ServiceRequestCreateDTO dto) {

        ServiceRequest sr = new ServiceRequest();
        sr.setRequestNumber("SR-" + UUID.randomUUID().toString().substring(0,6));
        sr.setVehicleId(dto.getVehicleId());
        sr.setCustomerId(dto.getCustomerId());
        sr.setPriority(dto.getPriority());
        sr.setIssue(dto.getIssue());

        ServiceRequest saved = serviceReqRepo.save(sr);

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
        
        ServiceBay bay=bayService.getBay(dto.getBayId());
        
        if (!bay.isAvailable())
            throw new RuntimeException("This bay is already occupied.");
        
        bayService.occupyBay(dto.getBayId());
    	
    	sr.setTechnicianId(dto.getTechnicianId());
    	sr.setBayId(dto.getBayId());
    	sr.setStatus(ServiceStatus.ASSIGNED);
    	
    	try {
    		serviceReqRepo.save(sr);
    	}catch(OptimisticLockingFailureException e) {
    		
    		bayService.releaseBay(dto.getBayId());
    		
            throw new RequestAlreadyAssignedException(
                    "This service request was already assigned by another manager."
                );    	
        }

    	return "Assigned successfully";

    }
    
    @Override
    public String updateStatus(String id,UpdateStatusDTO dto) {
    	
    	ServiceRequest sr=serviceReqRepo.findById(id)
    			.orElseThrow(()-> new RuntimeException("Request not found"));
    	
    	sr.setStatus(dto.getStatus());
    	serviceReqRepo.save(sr);
    	if(dto.getStatus() == ServiceStatus.COMPLETED) {
    	    bayService.releaseBay(sr.getBayId());
    	}
    	
    	return "Status updated";
    }
    
    @Override
    public String requestParts(String requestId, List<UsedPart> parts) {

        ServiceRequest sr = serviceReqRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if(sr.getStatus() != ServiceStatus.ASSIGNED && sr.getStatus() != ServiceStatus.IN_PROGRESS)
            throw new RuntimeException("Parts can be requested only after technician assignment");
        
        if(sr.getPartsStatus() == PartsStatus.PARTS_ISSUED)
            throw new RuntimeException("Parts already issued. Cannot modify.");

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

        // later we will add inventory + billing calls here

        sr.setPartsStatus(PartsStatus.PARTS_ISSUED);
        sr.setPartsIssuedBy(managerId);
        sr.setPartsIssuedAt(LocalDateTime.now());

        serviceReqRepo.save(sr);
        return "Parts approved and issued";
    }


    
    @Override
    public List<ServiceRequest> getByStatus(ServiceStatus status){
    	return serviceReqRepo.findByStatus(status);
    }
}
