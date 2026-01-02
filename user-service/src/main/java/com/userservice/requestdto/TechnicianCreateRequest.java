package com.userservice.requestdto;


import com.userservice.model.enums.Specialization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TechnicianCreateRequest {
	
	@NotBlank(message = "UserId is required")
    private String userId;
	
	@NotNull(message = "Specialization is required")
    private Specialization specialization;
	
	
    private boolean available;
   
}