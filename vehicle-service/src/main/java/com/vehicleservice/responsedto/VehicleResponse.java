package com.vehicleservice.responsedto;

import com.vehicleservice.model.VehicleType;

import lombok.Data;

@Data
public class VehicleResponse {

    private String id;
    private String ownerId;
    private String registrationNumber;
    private String make;
    private String model;
    private int year;
    private String color;
    private VehicleType type;
    private String description;
}
