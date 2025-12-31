package com.userservice.responsedto;

import com.userservice.model.enums.Specialization;

import lombok.Data;

@Data
public class TechnicianResponse {
    private String id;
    private String userId;
    private Specialization specialization;
    private boolean available;
    private String managerId;
}
