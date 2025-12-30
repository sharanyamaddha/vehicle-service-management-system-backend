package com.vehicleservice.model;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Vehicle {

	@Id
	private String id;
	
	@NotBlank(message = "OwnerId is required")
	private String ownerId;
	
    @NotBlank(message = "Registration number is required")
    @Pattern(
        regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$",
        message = "Registration number must be like KA01AB1234"
    )
    private String registrationNumber;
    
    @NotBlank(message = "Make is required")
    private String make;

    @NotBlank(message = "Model is required")
    private String model;

    @Min(value = 1990, message = "Year must be after 1990")
    private int year;

    @NotNull(message = "Vehicle type required")
    private VehicleType type;

    @NotBlank(message = "Color is required")
    private String color;
}
