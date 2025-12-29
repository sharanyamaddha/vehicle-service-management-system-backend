package com.userservice.requestdto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
