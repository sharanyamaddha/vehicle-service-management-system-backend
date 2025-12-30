package com.servicerequest.service;

import java.util.List;

import com.servicerequest.model.ServiceRequest;
import com.servicerequest.requestdto.AssignTechnicianDTO;
import com.servicerequest.requestdto.ServiceRequestCreateDTO;
import com.servicerequest.responsedto.ServiceRequestResponse;

public interface ServiceRequestService {

    ServiceRequestResponse createRequest(ServiceRequestCreateDTO dto);
    
    List<ServiceRequest> getCustomerRequests(String customerId);
    
    String assignTechnician(String id, AssignTechnicianDTO dto);
}
