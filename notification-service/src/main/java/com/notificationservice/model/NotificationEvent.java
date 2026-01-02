package com.notificationservice.model;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String type;
    private String message;
    private String email;
}
