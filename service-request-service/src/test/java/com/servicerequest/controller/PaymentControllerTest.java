package com.servicerequest.controller;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicerequest.requestdto.RazorpayVerifyRequest;
import com.servicerequest.responsedto.RazorpayOrderResponse;
import com.servicerequest.service.RazorpayPaymentService;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RazorpayPaymentService razorpayService;

    @InjectMocks
    private PaymentController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createOrder_Success() throws Exception {
        RazorpayOrderResponse res = new RazorpayOrderResponse(
                "order_123", 1000, "INR", "key", "inv1", "req1", "cust1", "desc", "user", "email", null);

        when(razorpayService.createOrder("inv1")).thenReturn(res);

        mockMvc.perform(post("/api/payments/razorpay/order/inv1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order_123"));
    }

    @Test
    void verify_Success() throws Exception {
        RazorpayVerifyRequest req = new RazorpayVerifyRequest();
        req.setOrderId("order_123");
        req.setPaymentId("pay_123");
        req.setSignature("sig_123");
        req.setInvoiceId("inv1");

        doNothing().when(razorpayService).verifyPayment(any(RazorpayVerifyRequest.class));

        mockMvc.perform(post("/api/payments/razorpay/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Payment verified"));
    }
}
