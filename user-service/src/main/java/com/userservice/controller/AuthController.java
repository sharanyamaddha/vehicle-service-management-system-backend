package com.userservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.model.enums.Role;
import com.userservice.requestdto.ChangePasswordRequest;
import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.requestdto.UpdateProfileRequest;
import com.userservice.responsedto.LoginResponse;
import com.userservice.responsedto.UserResponse;
import com.userservice.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req){

        if(req.getRole() != Role.CUSTOMER)
            throw new RuntimeException("Only customers can self-register");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerCustomer(req));
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {

        LoginResponse res = userService.login(req);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }
    
    
    
   
}
