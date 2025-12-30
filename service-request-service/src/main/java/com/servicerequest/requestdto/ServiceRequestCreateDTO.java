package com.servicerequest.requestdto;

import com.servicerequest.model.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceRequestCreateDTO {

    @NotBlank
    private String vehicleId;

    @NotBlank
    private String customerId;

    @NotNull
    private Priority priority;

    @NotBlank
    private String issue;
}

