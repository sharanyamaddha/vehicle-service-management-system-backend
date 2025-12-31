package com.billingservice.service;

import java.util.List;

import com.billingservice.model.Invoice;

public interface BillingService {

    Invoice generateInvoice(String serviceRequestId);
    List<Invoice> getInvoicesByCustomer(String customerId);
}
