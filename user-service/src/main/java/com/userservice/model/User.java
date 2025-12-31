package com.userservice.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.userservice.model.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Document(collection="users")
@Data
public class User {

	@Id
    private String id;
	
	@NotBlank(message = "Username is required")
	private String username;
	
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotNull(message = "Role is required")
    private Role role;    // CUSTOMER, TECHNICIAN, MANAGER, ADMIN
    
    private boolean active;
    
    @CreatedDate
    private LocalDateTime createdAt;
}
