package com.billingservice.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.billingservice.config.FeignConfig;
import com.billingservice.dto.ServiceRequestDTO;

@FeignClient(name = "service-request-service", configuration = FeignConfig.class)
public interface ServiceRequestClient {

    @GetMapping("/api/service-requests/{id}")
    ServiceRequestDTO getRequest(@PathVariable String id);
}

