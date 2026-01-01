package com.billingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor internalCallInterceptor() {
        return requestTemplate -> requestTemplate.header("X-Internal-Call", "true");
    }
}
