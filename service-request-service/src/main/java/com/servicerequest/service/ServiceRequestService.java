package com.servicerequest.service;

import java.util.List;
import java.util.Map;

import com.servicerequest.enums.ServiceStatus;
import com.servicerequest.model.ServiceRequest;
import com.servicerequest.model.UsedPart;
import com.servicerequest.requestdto.AssignTechnicianDTO;
import com.servicerequest.requestdto.ServiceRequestDTO;
import com.servicerequest.requestdto.UpdateStatusDTO;
import com.servicerequest.responsedto.ServiceRequestResponse;

public interface ServiceRequestService {

    String createRequest(ServiceRequestDTO dto);

    List<ServiceRequest> getCustomerRequests(String customerId);

    String assignTechnician(String id, AssignTechnicianDTO dto);

    String startJob(String id);

    String updateStatus(String id, UpdateStatusDTO dto);

    List<ServiceRequest> getByStatus(ServiceStatus status);

    String requestParts(String requestId, List<UsedPart> parts);

    String approveParts(String requestId, String managerId);

    List<ServiceRequest> getTechnicianRequests(String techId);

    String closeRequest(String id, Double laborCost);

    List<ServiceRequest> getAllRequests();

    ServiceRequest getById(String id);

    List<Map<String, Object>> getTechnicianPerformance();

    Map<String, Long> getTechnicianWorkload();
}
