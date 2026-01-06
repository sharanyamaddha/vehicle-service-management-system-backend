package com.servicerequest.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderResponse {
    private String orderId;
    private int amount; // in paise
    private String currency;
    private String key;
    private String invoiceId;
    private String serviceRequestId;
    private String customerId;
    private String description;
    private String customerName;
    private String customerEmail;
    private String customerContact;
}
