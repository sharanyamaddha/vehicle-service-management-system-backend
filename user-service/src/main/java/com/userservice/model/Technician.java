package com.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.userservice.model.enums.Specialization;

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
	    
	    private int currentJobs = 0;



}
