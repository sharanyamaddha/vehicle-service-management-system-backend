package com.billingservice.dto;

import java.util.List;

import lombok.Data;

@Data
public class ServiceRequestDTO {
    private String id;
    private String customerId;
    private List<UsedPartDTO> usedParts;
}

