package com.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.userservice.model.enums.Specialization;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Document(collection="technicians")
@Data
public class Technician {
	
	   @Id
	    private String id;

	    @NotBlank(message = "UserId is required")
	    private String userId;

	    @NotNull(message = "Specialization is required")
	    private Specialization specialization;

	    private boolean available;
	    
	    @Min(value = 0, message = "Current jobs cannot be negative")
	    private int currentJobs = 0;



}
