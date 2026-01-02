package com.userservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.userservice.responsedto.UserResponse;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ADMIN – view all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> all(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> byRole(@PathVariable String role){
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }


    // ADMIN – create MANAGER / TECHNICIAN
    @PostMapping("/internal/create")
    public ResponseEntity<Map<String,String>> createInternal(@RequestBody RegisterRequest req){
        return ResponseEntity.ok(Map.of(
            "message", userService.createInternalUser(req)
        ));
    }

    // ADMIN – disable user
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Map<String,String>> disable(@PathVariable String id){
        userService.disableUser(id);
        return ResponseEntity.ok(Map.of("message","User disabled"));
    }

    // ADMIN – enable user
    @PatchMapping("/{id}/enable")
    public ResponseEntity<Map<String,String>> enable(@PathVariable String id){
        userService.enableUser(id);
        return ResponseEntity.ok(Map.of("message","User enabled"));
    }
}
