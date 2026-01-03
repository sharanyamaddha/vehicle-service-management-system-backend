package com.servicerequest.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsedPart {

    @NotBlank(message = "Part ID is required")
    private String partId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int qty;
}