package com.servicerequest.requestdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignTechnicianDTO {

    @NotBlank
    private String technicianId;

    @Min(value = 1, message = "Bay number must be required")
    private int bayNumber;
}
