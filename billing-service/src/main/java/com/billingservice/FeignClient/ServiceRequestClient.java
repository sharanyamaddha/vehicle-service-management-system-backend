package com.billingservice.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.billingservice.dto.ServiceRequestDTO;

@FeignClient(name = "service-request-service")
public interface ServiceRequestClient {

    @GetMapping("/api/service-requests/{id}")
    ServiceRequestDTO getRequest(@PathVariable String id);
}

