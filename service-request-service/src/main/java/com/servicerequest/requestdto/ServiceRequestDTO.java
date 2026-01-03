package com.servicerequest.requestdto;

import com.servicerequest.enums.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceRequestDTO {

    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;


    @NotNull(message = "Priority is required")
    private Priority priority;


    @NotBlank(message = "Issue description is required")
    private String issue;
}

