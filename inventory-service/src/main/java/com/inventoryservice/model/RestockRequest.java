package com.inventoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "restock_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockRequest {
    @Id
    private String id;
    private String partId;
    private int quantity;
    private String managerId;
    private String reason;
    private String status; // REQUESTED, APPROVED, REJECTED
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
}
