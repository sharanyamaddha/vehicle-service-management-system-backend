package com.servicerequest.service;

import java.util.List;

import com.servicerequest.model.Invoice;


public interface BillingService {

    Invoice generateInvoice(String serviceRequestId);
    List<Invoice> getInvoicesByCustomer(String customerId);

    //String markAsPaid(String invoiceId);
}
