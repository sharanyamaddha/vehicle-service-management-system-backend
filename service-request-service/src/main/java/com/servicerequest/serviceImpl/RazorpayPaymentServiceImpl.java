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
import com.servicerequest.service.RazorpayAdapter;

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
    private RazorpayAdapter razorpayAdapter;

    @Autowired
    private ServiceRequestRepository serviceReqRepo;

    @Autowired
    private UserClient userClient;

    @Override
    public RazorpayOrderResponse createOrder(String invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new PaymentException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new PaymentException("Invoice is already paid");
        }

        // Validate Service Request
        ServiceRequest request = serviceReqRepo.findById(invoice.getServiceRequestId())
                .orElseThrow(() -> new PaymentException("Service Request not found"));

        if (request.getStatus() != ServiceStatus.CLOSED) {
            throw new PaymentException("Service Request must be CLOSED to pay");
        }

        try {
            JSONObject notes = new JSONObject();
            notes.put("invoiceId", invoice.getId());
            notes.put("customerId", invoice.getCustomerId());

            int amountPaise = (int) (invoice.getTotal() * 100);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
            orderRequest.put("notes", notes);

            Order order = razorpayAdapter.createRazorpayOrder(keyId, keySecret, orderRequest);

            // Persist mapping
            invoice.setRazorpayOrderId(order.get("id"));
            invoiceRepository.save(invoice);

            UserResponse customer = safeFetchUser(request.getCustomerId());

            return new RazorpayOrderResponse(
                    order.get("id"),
                    amountPaise,
                    "INR",
                    keyId,
                    invoice.getId(),
                    request.getId(),
                    request.getCustomerId(),
                    "Service Invoice " + request.getRequestNumber(),
                    customer != null ? customer.getUsername() : null,
                    customer != null ? customer.getEmail() : null,
                    null);

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
