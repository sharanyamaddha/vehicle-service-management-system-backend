package com.servicerequest.service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Component
public class RazorpayAdapter {

    public Order createRazorpayOrder(String key, String secret, JSONObject options) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(key, secret);
        return client.orders.create(options);
    }
}
