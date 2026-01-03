package com.inventoryservice.requestdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePartRequest {

    @NotBlank(message = "Part name is required")
    private String name;

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private int reorderLevel;
}
