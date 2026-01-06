package com.servicerequest.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.servicerequest.responsedto.UserResponse;

@FeignClient(name = "user-service")
public interface UserClient {

    @PutMapping("/api/technicians/{id}/increment")
    void increment(@PathVariable String id);

    @PutMapping("/api/technicians/{id}/decrement")
    void decrement(@PathVariable String id);

    @GetMapping("/api/users/{id}")
    UserResponse getUser(@PathVariable String id);
}
