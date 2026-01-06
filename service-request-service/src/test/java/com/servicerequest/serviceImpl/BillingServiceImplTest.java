package com.servicerequest.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.servicerequest.FeignClient.InventoryClient;
import com.servicerequest.enums.InvoiceStatus;
import com.servicerequest.model.Invoice;
import com.servicerequest.model.ServiceRequest;
import com.servicerequest.model.UsedPart;
import com.servicerequest.repository.InvoiceRepository;
import com.servicerequest.repository.ServiceRequestRepository;
import com.servicerequest.requestdto.InventoryPartDTO;

@ExtendWith(MockitoExtension.class)
public class BillingServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepo;

    @Mock
    private ServiceRequestRepository serviceRequestRepo;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private BillingServiceImpl billingService;

    private ServiceRequest mockRequest;
    private Invoice mockInvoice;

    @BeforeEach
    void setUp() {
        mockRequest = new ServiceRequest();
        mockRequest.setId("req1");
        mockRequest.setCustomerId("user1");
        mockRequest.setUsedParts(List.of());

        mockInvoice = new Invoice();
        mockInvoice.setId("inv1");
        mockInvoice.setServiceRequestId("req1");
        mockInvoice.setTotal(200.0);
        mockInvoice.setStatus(InvoiceStatus.PENDING);
    }

    @Test
    void generateInvoice_NoParts_Success() {
        when(invoiceRepo.existsByServiceRequestId("req1")).thenReturn(false);
        when(serviceRequestRepo.findById("req1")).thenReturn(Optional.of(mockRequest));
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(mockInvoice);

        Invoice res = billingService.generateInvoice("req1", 100.0);

        assertNotNull(res);
        verify(invoiceRepo).save(any(Invoice.class));
    }

    @Test
    void generateInvoice_WithParts_Success() {
        UsedPart part = new UsedPart();
        part.setPartId("p1");
        part.setQty(2);
        mockRequest.setUsedParts(List.of(part));

        InventoryPartDTO partDto = new InventoryPartDTO();
        partDto.setPrice(50.0);

        when(invoiceRepo.existsByServiceRequestId("req1")).thenReturn(false);
        when(serviceRequestRepo.findById("req1")).thenReturn(Optional.of(mockRequest));
        when(inventoryClient.getPart("p1")).thenReturn(partDto);
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(i -> i.getArguments()[0]);

        Invoice res = billingService.generateInvoice("req1", 100.0);

        // 100 (labor) + 2*50 (parts) = 200
        assertEquals(200.0, res.getTotal());
    }

    @Test
    void payInvoice_Success() {
        when(invoiceRepo.findById("inv1")).thenReturn(Optional.of(mockInvoice));

        billingService.payInvoice("inv1");

        assertEquals(InvoiceStatus.PAID, mockInvoice.getStatus());
        verify(invoiceRepo).save(mockInvoice);
    }

    @Test
    void payInvoice_AlreadyPaid() {
        mockInvoice.setStatus(InvoiceStatus.PAID);
        when(invoiceRepo.findById("inv1")).thenReturn(Optional.of(mockInvoice));

        assertThrows(RuntimeException.class, () -> billingService.payInvoice("inv1"));
    }
}
