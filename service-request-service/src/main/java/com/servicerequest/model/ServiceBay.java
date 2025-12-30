package com.servicerequest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "service_bays")
@Data
public class ServiceBay {

    @Id
    private String id;

    private boolean available = true;
}