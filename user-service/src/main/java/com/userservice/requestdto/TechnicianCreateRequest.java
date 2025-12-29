package com.userservice.requestdto;

import com.userservice.model.Specialization;

import lombok.Data;

@Data
public class TechnicianCreateRequest {
    private String userId;
    private Specialization specialization;
    private boolean available;
}