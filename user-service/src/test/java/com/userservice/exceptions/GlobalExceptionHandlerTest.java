package com.userservice.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleEmailExists_returnsConflict() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("Email exists");
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleEmailExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email exists", response.getBody().get("message"));
    }

    @Test
    void handleInvalidCredentials_returnsUnauthorized() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Invalid credentials");
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleInvalidCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().get("message"));
    }

    @Test
    void handleAccountNotActive_returnsForbidden() {
        AccountNotActiveException ex = new AccountNotActiveException("Account not active");
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleAccountNotActive(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Account not active", response.getBody().get("message"));
    }
}
