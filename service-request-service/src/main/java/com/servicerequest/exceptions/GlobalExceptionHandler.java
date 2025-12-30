package com.servicerequest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestAlreadyAssignedException.class)
    public ResponseEntity<Map<String,Object>> handleAssigned(RequestAlreadyAssignedException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            Map.of(
                //"timestamp", LocalDateTime.now(),
                "error", ex.getMessage()
            )
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleRuntime(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", ex.getMessage()
            )
        );
    }
}
