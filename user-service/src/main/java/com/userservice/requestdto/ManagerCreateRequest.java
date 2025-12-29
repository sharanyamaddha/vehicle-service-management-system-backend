package com.userservice.requestdto;

import lombok.Data;

@Data
public class ManagerCreateRequest {
    private String userId;
    private String bayGroup;
    private int maxTechnicians;
}

