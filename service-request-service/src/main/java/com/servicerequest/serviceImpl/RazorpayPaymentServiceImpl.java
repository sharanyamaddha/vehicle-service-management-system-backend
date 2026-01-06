package com.servicerequest.serviceImpl;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
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
import com.servicerequest.responsedto.UserResponse;
import com.servicerequest.service.RazorpayPaymentService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

@Service
public class RazorpayPaymentServiceImpl implements RazorpayPaymentService {

    @Value("${razorpay.key-id:}")
    private String keyId;

    @Value("${razorpay.key-secret:}")
    private String keySecret;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private UserClient userClient;

    @Override
    public RazorpayOrderResponse createOrder(String invoiceId) {
        validateConfig();

        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new PaymentException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new PaymentException("Invoice already paid");
        }

        ServiceRequest sr = serviceRequestRepository.findById(invoice.getServiceRequestId())
            .orElseThrow(() -> new PaymentException("Service request not found"));

        if (sr.getStatus() != ServiceStatus.CLOSED) {
            throw new PaymentException("Service request must be CLOSED before payment");
        }

        int amountPaise = (int) Math.round(invoice.getTotal() * 100);
        if (amountPaise <= 0) {
            throw new PaymentException("Invoice amount must be greater than zero");
        }

        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject notes = new JSONObject();
            notes.put("invoiceId", invoice.getId());
            notes.put("serviceRequestId", sr.getId());
            notes.put("customerId", sr.getCustomerId());

            JSONObject options = new JSONObject();
            options.put("amount", amountPaise);
            options.put("currency", "INR");
            options.put("receipt", invoice.getId());
            options.put("notes", notes);

            Order order = client.orders.create(options);

            // Persist mapping
            invoice.setRazorpayOrderId(order.get("id"));
            invoiceRepository.save(invoice);

            UserResponse customer = safeFetchUser(sr.getCustomerId());

            return new RazorpayOrderResponse(
                    order.get("id"),
                    amountPaise,
                    "INR",
                    keyId,
                    invoice.getId(),
                    sr.getId(),
                    sr.getCustomerId(),
                    "Service Invoice " + sr.getRequestNumber(),
                    customer != null ? customer.getUsername() : null,
                    customer != null ? customer.getEmail() : null,
                    null
            );

        } catch (RazorpayException ex) {
            throw new PaymentException("Failed to create Razorpay order: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void verifyPayment(RazorpayVerifyRequest request) {
        validateConfig();

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
            .orElseThrow(() -> new PaymentException("Invoice not found"));

        if (!request.getOrderId().equals(invoice.getRazorpayOrderId())) {
            throw new PaymentException("Order mismatch for invoice");
        }

        String payload = request.getOrderId() + "|" + request.getPaymentId();
        String expectedSignature = hmacSha256(payload, keySecret);

        if (!expectedSignature.equals(request.getSignature())) {
            throw new PaymentException("Invalid payment signature");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setRazorpayPaymentId(request.getPaymentId());
        invoice.setRazorpaySignature(request.getSignature());
        invoice.setPaidAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
    }

    private void validateConfig() {
        if (!StringUtils.hasText(keyId) || !StringUtils.hasText(keySecret)) {
            throw new PaymentException("Razorpay keys are not configured");
        }
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hmacData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacData);
        } catch (GeneralSecurityException ex) {
            throw new PaymentException("Failed to compute HMAC", ex);
        }
    }

    private UserResponse safeFetchUser(String customerId) {
        try {
            return userClient.getUser(customerId);
        } catch (Exception ignored) {
            return null;
        }
    }
}
