package com.userservice.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.userservice.exceptions.EmailAlreadyExistsException;
import com.userservice.exceptions.InvalidCredentialsException;
import com.userservice.model.User;
import com.userservice.model.Technician;
import com.userservice.model.enums.Role;
import com.userservice.model.enums.Specialization;
import com.userservice.repository.TechnicianRepository;
import com.userservice.repository.UserRepository;
import com.userservice.requestdto.ChangePasswordRequest;
import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.requestdto.UpdateProfileRequest;
import com.userservice.responsedto.LoginResponse;
import com.userservice.responsedto.UserResponse;
import com.userservice.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private TechnicianRepository technicianRepo;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId("1");
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setRole(Role.CUSTOMER);
        mockUser.setActive(true);
        mockUser.setPasswordSet(true);
    }

    @Test
    void registerCustomer_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setUsername("testuser");
        req.setPassword("password");
        req.setRole(Role.CUSTOMER);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("mockToken");

        LoginResponse response = userService.registerCustomer(req);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerCustomer_EmailExists() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.registerCustomer(req));
    }

    @Test
    void login_Success() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(mockUser)).thenReturn("mockToken");

        LoginResponse response = userService.login(req);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
    }

    @Test
    void login_InvalidPassword() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("wrongpassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(req));
    }

    @Test
    void createInternalUser_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("manager@example.com");
        req.setUsername("manager");
        req.setRole(Role.MANAGER);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        String result = userService.createInternalUser(req);

        assertEquals("Internal user invited. Email sent.", result);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById("1")).thenReturn(Optional.of(mockUser));
        UserResponse res = userService.getUserById("1");
        assertEquals("testuser", res.getUsername());
    }

    @Test
    void changeMyPassword_Success() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("old");
        req.setNewPassword("new");

        when(jwtUtil.extractUsername("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("old", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("newEncoded");

        userService.changeMyPassword("Bearer token", req);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void mapUser_Technician_Success() {
        // Arrange
        User techUser = new User();
        techUser.setId("2");
        techUser.setRole(Role.TECHNICIAN);
        techUser.setActive(true);
        techUser.setPasswordSet(true);

        Technician tech = new Technician();
        tech.setSpecialization(Specialization.ELECTRICAL);
        tech.setCurrentJobs(2);
        tech.setMaxDailyCapacity(5);
        tech.setAvailable(true);

        when(userRepository.findById("2")).thenReturn(Optional.of(techUser));
        when(technicianRepo.findByUserId("2")).thenReturn(Optional.of(tech));

        // Act
        UserResponse res = userService.getUserById("2");

        // Assert
        assertEquals("ELECTRICAL", res.getSpecialization());
        assertEquals(2, res.getWorkload());
    }
}
