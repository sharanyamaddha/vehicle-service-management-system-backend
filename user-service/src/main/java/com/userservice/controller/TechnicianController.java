package com.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.userservice.requestdto.TechnicianCreateRequest;
import com.userservice.responsedto.TechnicianResponse;
import com.userservice.service.TechnicianService;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    @Autowired
    private TechnicianService techService;

    // ADMIN – assign specialization
    @PostMapping
    public ResponseEntity<TechnicianResponse> create(@RequestBody TechnicianCreateRequest req){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(techService.createTechnician(req));
    }

    // MANAGER – view available technicians
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TechnicianResponse>> byStatus(@PathVariable boolean status){
        return ResponseEntity.ok(techService.getTechniciansByStatus(status));
    }
    
    @GetMapping("/specialization/{type}")
    public ResponseEntity<List<TechnicianResponse>> bySpecialization(@PathVariable String Specialization){
        return ResponseEntity.ok(techService.getAvailableBySpecialization(Specialization));
    }

    @PatchMapping("/technicians/{id}/increment")
    public void incrementWorkload(@PathVariable String id){
        techService.incrementWorkload(id);
    }

    @PatchMapping("/technicians/{id}/decrement")
    public void decrementWorkload(@PathVariable String id){
        techService.decrementWorkload(id);
    }


}
