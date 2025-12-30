package com.inventoryservice.requestdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePartRequest {
	
    @NotBlank(message = "Part name is required")
    private String name;

    @Min(value = 1, message = "Price must be greater than 0")
    private double price;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @Min(value = 1, message = "Reorder level must be at least 1")
    private int reorderLevel;
}
