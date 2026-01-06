package com.servicerequest.service;

import com.servicerequest.requestdto.RazorpayVerifyRequest;
import com.servicerequest.responsedto.RazorpayOrderResponse;

public interface RazorpayPaymentService {

    RazorpayOrderResponse createOrder(String invoiceId);

    void verifyPayment(RazorpayVerifyRequest request);
}
