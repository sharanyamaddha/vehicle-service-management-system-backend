package com.servicerequest.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "billing-service")
public interface BillingClient {

    @PostMapping("/api/invoices/generate/{serviceRequestId}")
    void generateInvoice(@PathVariable String serviceRequestId);
}

