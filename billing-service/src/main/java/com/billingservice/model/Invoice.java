package com.billingservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "invoices")
@Data
public class Invoice {

    @Id
    private String id;

    private String serviceRequestId;
    private String customerId;
    private double total;
    private String status;   // PENDING | PAID
}
