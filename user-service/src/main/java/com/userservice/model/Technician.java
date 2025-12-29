package com.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="technicians")
@Data
public class Technician {
    @Id
    private String id;
    private String userId;
    private Specialization specialization;
    private boolean available;
    private String managerId;
}
