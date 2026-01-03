package com.servicerequest.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.servicerequest.model.Invoice;
import com.servicerequest.service.BillingService;


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
    
    @PatchMapping("/{id}/pay")
    public ResponseEntity<String> payInvoice(@PathVariable String id){
        billingService.payInvoice(id);
        return ResponseEntity.ok("Payment successful");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable String id){
        return ResponseEntity.ok(billingService.getInvoiceById(id));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> all(){
        return ResponseEntity.ok(billingService.getAllInvoices());
    }


}
