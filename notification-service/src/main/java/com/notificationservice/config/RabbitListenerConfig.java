package com.notificationservice.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

@Configuration
public class RabbitListenerConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitListenerConfig.class);

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setAutoStartup(true);
        
        // Enable manual acknowledgment for better error handling
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        
        // Set concurrency
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        
        // Error handler for better debugging
        factory.setErrorHandler(new ConditionalRejectingErrorHandler());
        
        // Enable default requeue policy
        factory.setDefaultRequeueRejected(false);
        
        logger.info("✅ RabbitMQ Listener Container Factory configured successfully");

        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        converter.setClassMapper(classMapper);
        return converter;
    }
}
