package com.userservice.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection="users")
@Data
public class User {

	@Id
    private String id;
    private String email;
    private String password;
    private Role role;    // CUSTOMER, TECHNICIAN, MANAGER, ADMIN
    private boolean active;
    private LocalDateTime createdAt;
}
