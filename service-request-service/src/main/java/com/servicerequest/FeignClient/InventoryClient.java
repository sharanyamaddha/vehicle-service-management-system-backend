package com.servicerequest.FeignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.servicerequest.model.UsedPart;
import com.servicerequest.requestdto.InventoryPartDTO;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping("/api/parts/deduct")
    void deductStock(@RequestBody List<UsedPart> parts);
    
    @GetMapping("/api/parts/{id}")
    InventoryPartDTO getPart(@PathVariable String id);
}
