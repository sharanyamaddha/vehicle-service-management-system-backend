package com.servicerequest.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.servicerequest.enums.InvoiceStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Document(collection = "invoices")
@Data
public class Invoice {

	 @Id
	    private String id;

	    @NotBlank(message = "Service Request ID is required")
	    private String serviceRequestId;

	    @NotBlank(message = "Customer ID is required")
	    private String customerId;

	    @Min(value = 0, message = "Invoice total cannot be negative")
	    private double total;

	    @NotNull(message = "Invoice status is required")
	    private InvoiceStatus status;   // PENDING | PAID

	    @CreatedDate
	    private LocalDateTime createdAt;
}
