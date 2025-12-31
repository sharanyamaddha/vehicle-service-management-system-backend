package com.servicerequest.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsedPartRequest {

    @NotBlank
    private String partId;

    @Min(1)
    private int qty;
}