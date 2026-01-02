package com.inventoryservice.controller;

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

import com.inventoryservice.model.InventoryPart;
import com.inventoryservice.requestdto.CreatePartRequest;
import com.inventoryservice.requestdto.UsedPartRequest;
import com.inventoryservice.service.InventoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/parts")
public class InventoryController {
	
	@Autowired
	private InventoryService inventoryService;
	
	@PostMapping
	public ResponseEntity<InventoryPart> addPart(@Valid @RequestBody CreatePartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.addPart(request));
    }
	
    @GetMapping
    public ResponseEntity<List<InventoryPart>> getAllParts() {
        return ResponseEntity.ok(inventoryService.getAllParts());
    }
    
    @GetMapping("/{id}")
    public InventoryPart getPartById(@PathVariable String id) {
        return inventoryService.getPartById(id);
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deductStock(@RequestBody List<UsedPartRequest> usedParts){
        inventoryService.deductStock(usedParts);
        return ResponseEntity.ok("Stock updated successfully");
    }
    
    @GetMapping("/alerts/low-stock")
    public ResponseEntity<List<InventoryPart>> lowStockAlerts() {
        return ResponseEntity.ok(inventoryService.getLowStockParts());
    }

}
