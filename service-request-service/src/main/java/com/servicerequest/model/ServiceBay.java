package com.servicerequest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Document(collection = "service_bays")
@Data
public class ServiceBay {

    @Id
    private String id;
    
    @Min(value = 1, message = "Bay number must be required")
    private int bayNumber;
    
    private boolean available = true;
}