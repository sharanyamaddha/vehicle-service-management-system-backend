package com.inventoryservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Document(collection = "inventory_parts")
@Data
public class InventoryPart {
	
    @Id
    private String id;          

    @NotBlank(message = "Part name is required")
    private String name;

    @Min(value = 1, message = "Price must be greater than 0")
    private double price;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @Min(value = 1, message = "Reorder level must be at least 1")
    private int reorderLevel;

}
