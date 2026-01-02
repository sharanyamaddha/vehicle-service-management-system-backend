package com.servicerequest.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.servicerequest.enums.InvoiceStatus;

import lombok.Data;

@Document(collection = "invoices")
@Data
public class Invoice {

    @Id
    private String id;

    private String serviceRequestId;

    private String customerId;

    private double total;

    private InvoiceStatus status;   // PENDING | PAID

    private LocalDateTime createdAt;
}
