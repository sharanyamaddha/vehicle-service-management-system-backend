package com.servicerequest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.servicerequest.requestdto.RazorpayVerifyRequest;
import com.servicerequest.responsedto.RazorpayOrderResponse;
import com.servicerequest.service.RazorpayPaymentService;

@RestController
@RequestMapping("/api/payments/razorpay")
public class PaymentController {

    @Autowired
    private RazorpayPaymentService razorpayPaymentService;

    @PostMapping("/order/{invoiceId}")
    public ResponseEntity<RazorpayOrderResponse> createOrder(@PathVariable String invoiceId) {
        return ResponseEntity.ok(razorpayPaymentService.createOrder(invoiceId));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Validated @RequestBody RazorpayVerifyRequest request) {
        razorpayPaymentService.verifyPayment(request);
        return ResponseEntity.ok("Payment verified");
    }
}
