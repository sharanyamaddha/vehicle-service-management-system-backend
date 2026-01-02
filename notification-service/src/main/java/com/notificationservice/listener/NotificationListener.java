package com.notificationservice.listener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notificationservice.config.RabbitConfig;
import com.notificationservice.model.NotificationEvent;
import com.notificationservice.service.EmailService;



@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    @Autowired
    private EmailService emailService;

    @RabbitListener(
    	    queues = RabbitConfig.QUEUE,
    	    messageConverter = "jackson2Converter"
    	)
    public void receive(Map<String, Object> payload) {

    	 String type = (String) payload.get("type");
    	    String email = (String) payload.get("email");
    	    Map<String,Object> data = (Map<String,Object>) payload.get("data");

    	    System.out.println("EVENT = " + type);
    	    System.out.println("EMAIL = " + email);
    	    System.out.println("DATA  = " + data);
    	    
    	    String body = emailService.buildServiceBookedMail(data);        
    	    emailService.sendMail(
                String.valueOf(payload.get("email")),
                "Service Request Confirmed – Vehicle Service System",
                body
        );
    }

}
