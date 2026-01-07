package com.servicerequest.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.razorpay.Order;
import com.servicerequest.FeignClient.UserClient;
import com.servicerequest.enums.InvoiceStatus;
import com.servicerequest.enums.ServiceStatus;
import com.servicerequest.exceptions.PaymentException;
import com.servicerequest.model.Invoice;
import com.servicerequest.model.ServiceRequest;
import com.servicerequest.repository.InvoiceRepository;
import com.servicerequest.repository.ServiceRequestRepository;
import com.servicerequest.requestdto.RazorpayVerifyRequest;
import com.servicerequest.responsedto.RazorpayOrderResponse;
import com.servicerequest.service.RazorpayAdapter;

@ExtendWith(MockitoExtension.class)
class RazorpayPaymentServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private RazorpayAdapter razorpayAdapter;

    @InjectMocks
    private RazorpayPaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "keyId", "testKey");
        ReflectionTestUtils.setField(service, "keySecret", "testSecret");
    }

    @Test
    void createOrder_Success() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId("inv1");
        invoice.setServiceRequestId("req1");
        invoice.setTotal(100.0);
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setCustomerId("cust1");

        ServiceRequest sr = new ServiceRequest();
        sr.setId("req1");
        sr.setStatus(ServiceStatus.CLOSED);
        sr.setCustomerId("cust1");
        sr.setRequestNumber("REQ-001");

        when(invoiceRepository.findById("inv1")).thenReturn(Optional.of(invoice));
        when(serviceRequestRepository.findById("req1")).thenReturn(Optional.of(sr));

        JSONObject jsonOrder = new JSONObject();
        jsonOrder.put("id", "order_123");
        Order mockOrder = new Order(jsonOrder);

        when(razorpayAdapter.createRazorpayOrder(anyString(), anyString(), any(JSONObject.class)))
                .thenReturn(mockOrder);

        RazorpayOrderResponse response = service.createOrder("inv1");

        assertNotNull(response);
        assertEquals("order_123", response.getOrderId());
        assertEquals(10000, response.getAmount());
        assertEquals("testKey", response.getKey());

        verify(invoiceRepository).save(invoice);
    }

    @Test
    void createOrder_InvoiceNotFound() {
        when(invoiceRepository.findById("inv1")).thenReturn(Optional.empty());
        assertThrows(PaymentException.class, () -> service.createOrder("inv1"));
    }

    @Test
    void createOrder_AlreadyPaid() {
        Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.PAID);
        when(invoiceRepository.findById("inv1")).thenReturn(Optional.of(invoice));

        assertThrows(PaymentException.class, () -> service.createOrder("inv1"));
    }

    @Test
    void verifyPayment_Success() throws Exception {
        RazorpayVerifyRequest req = new RazorpayVerifyRequest();
        req.setInvoiceId("inv1");
        req.setOrderId("order_123");
        req.setPaymentId("pay_123");

        String sig = calculateHmac("order_123|pay_123", "testSecret");
        req.setSignature(sig);

        Invoice invoice = new Invoice();
        invoice.setId("inv1");
        invoice.setRazorpayOrderId("order_123");

        when(invoiceRepository.findById("inv1")).thenReturn(Optional.of(invoice));

        service.verifyPayment(req);

        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertEquals("pay_123", invoice.getRazorpayPaymentId());
        verify(invoiceRepository).save(invoice);
    }

    private String calculateHmac(String data, String secret) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hmacData = mac.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return java.util.HexFormat.of().formatHex(hmacData);
    }

    @Test
    void verifyPayment_InvalidSignature() {
        RazorpayVerifyRequest req = new RazorpayVerifyRequest();
        req.setInvoiceId("inv1");
        req.setOrderId("order_123");
        req.setPaymentId("pay_123");
        req.setSignature("invalid_signature");

        Invoice invoice = new Invoice();
        invoice.setId("inv1");
        invoice.setRazorpayOrderId("order_123");

        when(invoiceRepository.findById("inv1")).thenReturn(Optional.of(invoice));

        assertThrows(PaymentException.class, () -> service.verifyPayment(req));
    }
}
