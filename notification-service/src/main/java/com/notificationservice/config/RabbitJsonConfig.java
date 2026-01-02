package com.notificationservice.config;

import java.util.HashMap;

import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitJsonConfig {

    @Bean
    public JacksonJsonMessageConverter jackson2Converter() {

        DefaultClassMapper mapper = new DefaultClassMapper();

        // FORCE all messages to Map
        mapper.setTrustedPackages("*");

        HashMap<String, Class<?>> idMapping = new HashMap<>();
        idMapping.put("java.util.Map", HashMap.class);
        mapper.setIdClassMapping(idMapping);

        JacksonJsonMessageConverter conv = new JacksonJsonMessageConverter();
        conv.setClassMapper(mapper);

        return conv;
    }
}
