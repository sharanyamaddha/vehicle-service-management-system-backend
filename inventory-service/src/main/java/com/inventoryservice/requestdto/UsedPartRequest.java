package com.inventoryservice.requestdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsedPartRequest {
    @NotBlank(message = "Part ID is required")
    private String partId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int qty;
}