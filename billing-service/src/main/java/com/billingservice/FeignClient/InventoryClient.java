package com.billingservice.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.billingservice.dto.InventoryPartDTO;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/parts/{id}")
    InventoryPartDTO getPart(@PathVariable String id);
}

