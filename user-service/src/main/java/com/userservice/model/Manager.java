package com.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Document(collection = "managers")
@Data
public class Manager {

    @Id
    private String id;

    @NotBlank(message = "User ID is mandatory")
    private String userId;

    @NotBlank(message = "Bay group is required")
    private String bayGroup;

    @NotNull(message = "Max technicians must be provided")
    @Min(value = 1, message = "Max technicians must be at least 1")
    private Integer maxTechnicians;
}
