package com.vehicleservice.requestdto;

import com.vehicleservice.model.VehicleType;

import lombok.Data;

@Data
public class VehicleRequest {

    private String ownerId;
    private String registrationNumber;
    private String make;
    private String model;
    private int year;
    private VehicleType type;
    private String color;
}
