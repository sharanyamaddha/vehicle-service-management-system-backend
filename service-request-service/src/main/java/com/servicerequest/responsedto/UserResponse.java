package com.servicerequest.responsedto;

import lombok.Data;

@Data
public class UserResponse {
    private String username;
    private String email;
    private String role;
    private String specialization;
    private int workload;
    private int maxCapacity;
}
