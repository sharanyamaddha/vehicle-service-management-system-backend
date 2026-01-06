package com.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.model.enums.Role;
import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.responsedto.LoginResponse;
import com.userservice.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {

        if (req.getRole() != Role.CUSTOMER)
            throw new RuntimeException("Only customers can self-register");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerCustomer(req));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {

        LoginResponse res = userService.login(req);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/activate")
    public ResponseEntity<java.util.Map<String, String>> activate(
            @Valid @RequestBody com.userservice.requestdto.ActivateAccountRequest req) {
        String msg = userService.activateAccount(req.getToken(), req.getPassword());
        return ResponseEntity.ok(java.util.Map.of("message", msg));
    }

}
