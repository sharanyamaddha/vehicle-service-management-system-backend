package com.servicerequest.requestdto;

import com.servicerequest.model.ServiceStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusDTO {

    @NotNull
    private ServiceStatus status;
}
