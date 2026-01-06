package com.servicerequest.responsedto;

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
}
