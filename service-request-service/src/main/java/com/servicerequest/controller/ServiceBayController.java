package com.servicerequest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.servicerequest.model.ServiceBay;
import com.servicerequest.service.ServiceBayService;

@RestController
@RequestMapping("/api/bays")
public class ServiceBayController {
	
	@Autowired
	private ServiceBayService bayService;
	
	@PostMapping
	public ResponseEntity<ServiceBay> create(@RequestBody ServiceBay bay){
		return ResponseEntity.status(HttpStatus.CREATED).body(bayService.createBay(bay));
	}
	
    @GetMapping("/available")
    public ResponseEntity<List<ServiceBay>> available(){
        return ResponseEntity.ok(bayService.getAvailableBays());
    }
    
    @PatchMapping("/{id}/occupy")
    public Map<String,String> occupy(@PathVariable String id){
        bayService.occupyBay(id);
        return Map.of("message","Bay occupied");
    }
    
    @PatchMapping("/{id}/release")
    public Map<String,String> release(@PathVariable String id){
        bayService.releaseBay(id);
        return Map.of("message","Bay released");
    }


}
