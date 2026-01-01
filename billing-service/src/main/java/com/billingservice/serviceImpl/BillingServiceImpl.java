package com.billingservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.billingservice.FeignClient.InventoryClient;
import com.billingservice.FeignClient.ServiceRequestClient;
import com.billingservice.dto.InventoryPartDTO;
import com.billingservice.dto.ServiceRequestDTO;
import com.billingservice.dto.UsedPartDTO;
import com.billingservice.model.Invoice;
import com.billingservice.repository.InvoiceRepository;
import com.billingservice.service.BillingService;
import com.netflix.discovery.converters.Auto;

@Service
public class BillingServiceImpl implements BillingService{
	
	@Autowired
	private InvoiceRepository invoiceRepo;
	
	@Autowired
	private InventoryClient inventoryClient;
	
	@Autowired
	private ServiceRequestClient requestClient;
	
	@Override
	public Invoice generateInvoice(String serviceRequestId) {

	    if(invoiceRepo.existsByServiceRequestId(serviceRequestId))
	        throw new RuntimeException("Invoice already generated for this service");

	    ServiceRequestDTO sr = requestClient.getRequest(serviceRequestId);

	    double total = 0;

	    for(UsedPartDTO part : sr.getUsedParts()){
	        InventoryPartDTO inv = inventoryClient.getPart(part.getPartId());
	        total += inv.getPrice() * part.getQty();
	    }

	    Invoice invoice = new Invoice();
	    invoice.setServiceRequestId(serviceRequestId);
	    invoice.setCustomerId(sr.getCustomerId());
	    invoice.setTotal(total);
	    invoice.setStatus("PENDING");

	    return invoiceRepo.save(invoice);
	}

    
    @Override
    public List<Invoice> getInvoicesByCustomer(String customerId) {
        return invoiceRepo.findByCustomerId(customerId);
    }

}
