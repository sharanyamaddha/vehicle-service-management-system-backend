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
    private UserClient userClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Invoice generateInvoice(String serviceRequestId) {

        if (invoiceRepo.existsByServiceRequestId(serviceRequestId))
            throw new RuntimeException("Invoice already generated for this service request");

        ServiceRequest sr = serviceRequestRepo.findById(serviceRequestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        double total = 0;

        for (UsedPart part : sr.getUsedParts()) {
            double price = inventoryClient.getPart(part.getPartId()).getPrice();
            total += price * part.getQty();
        }

        Invoice invoice = new Invoice();
        invoice.setServiceRequestId(sr.getId());
        invoice.setCustomerId(sr.getCustomerId());
        invoice.setTotal(total);
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setCreatedAt(LocalDateTime.now());

        Invoice saved = invoiceRepo.save(invoice);

        String customerEmail = userClient.getUser(sr.getCustomerId()).getEmail();
        publishEvent("INVOICE_GENERATED",
                "Invoice generated for your service request",
                customerEmail);

        return saved;
    }

    @Override
    public List<Invoice> getInvoicesByCustomer(String customerId) {
        return invoiceRepo.findByCustomerId(customerId);
    }

    private void publishEvent(String type, String message, String email) {

        NotificationEvent event = new NotificationEvent();
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("message", message);
        payload.put("email", email);

        rabbitTemplate.convertAndSend(
                NotificationConstants.EXCHANGE,
                NotificationConstants.ROUTING_KEY,
                payload
        );
    }
}
