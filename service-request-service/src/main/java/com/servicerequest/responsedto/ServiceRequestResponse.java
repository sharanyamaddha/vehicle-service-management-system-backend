package com.servicerequest.responsedto;

import com.servicerequest.model.ServiceStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceRequestResponse {

    private String id;
    private String requestNumber;
    private ServiceStatus status;
}
