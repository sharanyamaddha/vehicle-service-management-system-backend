package com.userservice.requestdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
	
    @NotBlank(message = "Username cannot be empty")
    private String username;
}


