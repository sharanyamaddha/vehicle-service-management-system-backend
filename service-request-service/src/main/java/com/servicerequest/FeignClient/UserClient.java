package com.servicerequest.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.servicerequest.config.FeignHttpConfig;

@FeignClient(name="user-service",configuration = FeignHttpConfig.class
)
public interface UserClient {

    @PatchMapping("/api/users/technicians/{id}/increment")
    void increment(@PathVariable String id);

    @PatchMapping("/api/users/technicians/{id}/decrement")
    void decrement(@PathVariable String id);
}

