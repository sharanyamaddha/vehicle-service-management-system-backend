package com.vehicleservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vehicleservice.requestdto.VehicleRequest;
import com.vehicleservice.responsedto.VehicleResponse;
import com.vehicleservice.service.VehicleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

	   @Autowired
	    private VehicleService vehicleService;
	   
	   @PostMapping
	    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest req){
	        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.addVehicle(req));
	    }
	   
	    @GetMapping("/customer/{id}")
	    public ResponseEntity<List<VehicleResponse>> byCustomer(@PathVariable String id){
	        return ResponseEntity.ok(vehicleService.getVehiclesByCustomer(id));
	    }
	    
	    @DeleteMapping("/{id}")
	    public ResponseEntity<Map<String,String>> delete(@PathVariable String id){
	        return ResponseEntity.ok(Map.of("message", vehicleService.deleteVehicle(id)));
	    }
	    
	    
	   
	   
	   
	   
}
