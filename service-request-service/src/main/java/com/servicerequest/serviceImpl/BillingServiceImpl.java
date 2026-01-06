package com.servicerequest.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.servicerequest.FeignClient.InventoryClient;
import com.servicerequest.FeignClient.UserClient;
import com.servicerequest.enums.InvoiceStatus;
import com.servicerequest.event.NotificationEvent;
import com.servicerequest.messaging.NotificationConstants;
import com.servicerequest.model.Invoice;
import com.servicerequest.model.ServiceRequest;
import com.servicerequest.model.UsedPart;
import com.servicerequest.repository.InvoiceRepository;
import com.servicerequest.repository.ServiceRequestRepository;
import com.servicerequest.service.BillingService;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private ServiceRequestRepository serviceRequestRepo;

    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Invoice generateInvoice(String serviceRequestId, Double laborCost) {

        if (invoiceRepo.existsByServiceRequestId(serviceRequestId))
            throw new RuntimeException("Invoice already generated for this service request");

        ServiceRequest sr = serviceRequestRepo.findById(serviceRequestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        double total = (laborCost != null ? laborCost : 0.0);

        String note = null;
        for (UsedPart part : sr.getUsedParts()) {
            var partDto = inventoryClient.getPart(part.getPartId());
            if (partDto.isFallback()) {
                note = "Warning: Inventory Service was unavailable. Parts cost is 0.0.";
            }
            double price = partDto.getPrice();
            total += price * part.getQty();
        }

        Invoice invoice = new Invoice();
        invoice.setNotes(note);
        invoice.setServiceRequestId(sr.getId());
        invoice.setCustomerId(sr.getCustomerId());
        invoice.setLaborCost(laborCost);
        invoice.setTotal(total);
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setCreatedAt(LocalDateTime.now());

        Invoice saved = invoiceRepo.save(invoice);

        return saved;
    }

    @Override
    public List<Invoice> getInvoicesByCustomer(String customerId) {
        return invoiceRepo.findByCustomerId(customerId);
    }

    @Override
    public void payInvoice(String id) {

        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID)
            throw new RuntimeException("Invoice already paid");

        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepo.save(invoice);
    }

    @Override
    public Invoice getInvoiceById(String id) {
        return invoiceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepo.findAll();
    }

    @Override
    public Map<String, Object> getRevenueStats() {
        List<Invoice> all = invoiceRepo.findAll();
        double totalRevenue = all.stream().mapToDouble(Invoice::getTotal).sum();
        double pendingRevenue = all.stream()
                .filter(i -> i.getStatus() == InvoiceStatus.PENDING)
                .mapToDouble(Invoice::getTotal).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("pendingRevenue", pendingRevenue);
        stats.put("totalInvoices", all.size());
        stats.put("paidInvoices", all.stream().filter(i -> i.getStatus() == InvoiceStatus.PAID).count());
        return stats;
    }
}
