package com.userservice.requestdto;

import com.userservice.model.Role;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private Role role;
}
