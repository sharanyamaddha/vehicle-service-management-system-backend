package com.servicerequest.event;

import lombok.Data;

@Data
public class NotificationEvent {
    private String type;
    private String message;
    private String email;
}

