package com.userservice.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.userservice.model.User;
import com.userservice.model.enums.Role;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId("testId");
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.CUSTOMER);

        // Use a fixed key for consistency or rely on the one in JwtUtil if permissible.
        // Since JwtUtil generates key from a constant string, we can rely on it.
    }

    @Test
    void generateToken_Success() {
        String token = jwtUtil.generateToken(mockUser);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_Success() {
        String token = jwtUtil.generateToken(mockUser);
        String username = jwtUtil.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void extractRole_Success() {
        String token = jwtUtil.generateToken(mockUser);
        String role = jwtUtil.extractRole(token);
        assertEquals("CUSTOMER", role);
    }

    @Test
    void extractUserId_Success() {
        String token = jwtUtil.generateToken(mockUser);
        String userId = jwtUtil.extractUserId(token);
        assertEquals("testId", userId);
    }

    @Test
    void isTokenExpired_False() {
        String token = jwtUtil.generateToken(mockUser);
        assertFalse(jwtUtil.isTokenExpired(token));
    }
}
