package com.inventoryservice.requestdto;

import lombok.Data;

@Data
public class CreateRestockRequest {
    private String partId;
    private int quantity;
    private String managerId;
    private String reason;
}
