package com.servicerequest.FeignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.servicerequest.model.UsedPartRequest;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping("/api/parts/deduct")
    void deductStock(@RequestBody List<UsedPartRequest> parts);
}
