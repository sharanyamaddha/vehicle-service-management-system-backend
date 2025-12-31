package com.billingservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billingservice.model.Invoice;
import com.billingservice.service.BillingService;

@RestController
@RequestMapping("/api/invoices")
public class BillingController {

	@Autowired
	private BillingService billingService;
	
    @PostMapping("/generate/{serviceRequestId}")
    public Invoice generate(@PathVariable String serviceRequestId){
        return billingService.generateInvoice(serviceRequestId);
    }
    
    @GetMapping("/customer/{id}")
    public List<Invoice> getByCustomer(@PathVariable String id){
        return billingService.getInvoicesByCustomer(id);
    }
}
