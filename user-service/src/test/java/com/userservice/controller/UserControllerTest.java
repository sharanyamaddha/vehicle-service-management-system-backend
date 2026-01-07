package com.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.requestdto.ChangePasswordRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.requestdto.UpdateProfileRequest;
import com.userservice.responsedto.UserResponse;
import com.userservice.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllUsers_Success() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(new UserResponse()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getUserById_Success() throws Exception {
        UserResponse response = new UserResponse();
        response.setUsername("testuser");
        when(userService.getUserById("1")).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void createInternal_Success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("manager@example.com");

        when(userService.createInternalUser(any(RegisterRequest.class))).thenReturn("User invited");

        mockMvc.perform(post("/api/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User invited"));
    }

    @Test
    void disableUser_Success() throws Exception {
        doNothing().when(userService).disableUser("1");

        mockMvc.perform(patch("/api/users/1/disable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User disabled"));
    }

    @Test
    void byRole_Success() throws Exception {
        when(userService.getUsersByRole("MANAGER")).thenReturn(List.of(new UserResponse()));
        mockMvc.perform(get("/api/users/role/MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void enable_Success() throws Exception {
        doNothing().when(userService).enableUser("1");
        mockMvc.perform(patch("/api/users/1/enable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User enabled"));
    }

    @Test
    void disabledUsers_Success() throws Exception {
        when(userService.getDisabledUsers()).thenReturn(List.of(new UserResponse()));
        mockMvc.perform(get("/api/users/disabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void invitedUsers_Success() throws Exception {
        when(userService.getInvitedUsers()).thenReturn(List.of(new UserResponse()));
        mockMvc.perform(get("/api/users/invited"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void me_Success() throws Exception {
        UserResponse ur = new UserResponse();
        ur.setUsername("me");
        when(userService.getMyProfile("Bearer token")).thenReturn(ur);
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("me"));
    }

    @Test
    void updateProfile_Success() throws Exception {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setUsername("newuser");

        doNothing().when(userService).updateMyProfile(eq("Bearer token"), any(UpdateProfileRequest.class));

        mockMvc.perform(put("/api/users/me")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated"));
    }

    @Test
    void changePassword_Success() throws Exception {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("old");
        req.setNewPassword("newPassword123");

        doNothing().when(userService).changeMyPassword(eq("Bearer token"), any(ChangePasswordRequest.class));

        mockMvc.perform(patch("/api/users/me/password")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated"));
    }

    @Test
    void resetPassword_Success() throws Exception {
        doNothing().when(userService).resetPassword("1");
        mockMvc.perform(patch("/api/users/1/reset-password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset to default"));
    }

    @Test
    void resendInvite_Success() throws Exception {
        doNothing().when(userService).resendInvite("1");
        mockMvc.perform(post("/api/users/1/resend-invite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Invite link sent again to email."));
    }
}
