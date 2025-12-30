package com.servicerequest.serviceImpl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.servicerequest.model.ServiceRequest;
import com.servicerequest.repository.ServiceRequestRepository;
import com.servicerequest.requestdto.ServiceRequestCreateDTO;
import com.servicerequest.responsedto.ServiceRequestResponse;
import com.servicerequest.service.ServiceRequestService;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

	@Autowired
	private ServiceRequestRepository serviceReqRepo;
	
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
}
