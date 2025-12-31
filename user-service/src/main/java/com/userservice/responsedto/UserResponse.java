package com.userservice.responsedto;

import com.userservice.model.enums.Role;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String username;     
    private String email;
    private Role role;
    private boolean active;
}

