package com.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "managers")
@Data
public class Manager {

    @Id
    private String id;

    private String userId;
    private String bayGroup;
    private int maxTechnicians;
}
