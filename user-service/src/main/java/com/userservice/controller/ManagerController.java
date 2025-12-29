package com.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.model.Manager;
import com.userservice.requestdto.ManagerCreateRequest;
import com.userservice.service.ManagerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/managers")
public class ManagerController {

    @Autowired
    private ManagerService service;

    @PostMapping
    public ResponseEntity<Manager> create(@Valid @RequestBody ManagerCreateRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createManager(req));
    }

    @GetMapping
    public ResponseEntity<List<Manager>> getAll(){
        return ResponseEntity.ok(service.getAllManagers());
    }
}

