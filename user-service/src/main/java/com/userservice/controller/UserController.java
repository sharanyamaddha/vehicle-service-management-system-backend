package com.userservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.responsedto.UserResponse;
import com.userservice.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    

    @GetMapping
    public ResponseEntity<List<UserResponse>> all(){ 
    	return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
    	return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<UserResponse>> pending(){ 
    	return ResponseEntity.ok(userService.getPendingUsers()); 
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Map<String,String>> approve(@PathVariable String id){
        userService.approveUser(id);
        return ResponseEntity.ok(Map.of("message", "User approved successfully"));
    }

    
}
