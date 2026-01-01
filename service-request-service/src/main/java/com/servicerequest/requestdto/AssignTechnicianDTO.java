package com.servicerequest.requestdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignTechnicianDTO {

    @NotBlank
    private String technicianId;

    private int bayNumber;
}
