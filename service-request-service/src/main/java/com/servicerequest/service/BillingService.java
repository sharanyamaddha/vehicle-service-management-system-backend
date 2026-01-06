package com.servicerequest.service;

import java.util.List;
import java.util.Map;

import com.servicerequest.model.Invoice;

public interface BillingService {

    Invoice generateInvoice(String serviceRequestId, Double laborCost);

    List<Invoice> getInvoicesByCustomer(String customerId);

    void payInvoice(String id);

    Invoice getInvoiceById(String id);

    Map<String, Object> getRevenueStats();

    List<Invoice> getAllInvoices();

}
