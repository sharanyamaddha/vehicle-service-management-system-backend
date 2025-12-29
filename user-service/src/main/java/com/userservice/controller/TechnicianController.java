package com.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.model.Technician;
import com.userservice.requestdto.TechnicianCreateRequest;
import com.userservice.responsedto.TechnicianResponse;
import com.userservice.service.TechnicianService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

	@Autowired
    private TechnicianService techService;
	

    @PostMapping
    public ResponseEntity<TechnicianResponse> create(@Valid @RequestBody TechnicianCreateRequest req){
        return ResponseEntity.status(HttpStatus.CREATED)
        		.body(techService.createTechnician(req));
    }


    @GetMapping("/manager/{id}")
    public ResponseEntity<List<TechnicianResponse>> byManager(@PathVariable String managerid){
        return ResponseEntity.ok(techService.getTechniciansByManager(managerid));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TechnicianResponse>> byStatus(@PathVariable boolean status){
        return ResponseEntity.ok(techService.getTechniciansByStatus(status));
    }
}
