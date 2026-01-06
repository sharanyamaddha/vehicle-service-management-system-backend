package com.servicerequest.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.servicerequest.responsedto.VehicleResponse;

@FeignClient(name = "vehicle-service")
public interface VehicleClient {

    @GetMapping("/api/vehicles/{id}")
    VehicleResponse getVehicleById(@PathVariable("id") String id);
}
