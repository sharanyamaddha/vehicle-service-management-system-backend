package com.servicerequest.service;

import java.util.List;

import com.servicerequest.model.Invoice;


public interface BillingService {

    Invoice generateInvoice(String serviceRequestId);
    List<Invoice> getInvoicesByCustomer(String customerId);

    void payInvoice(String id);

    Invoice getInvoiceById(String id);

    List<Invoice> getAllInvoices();

}
