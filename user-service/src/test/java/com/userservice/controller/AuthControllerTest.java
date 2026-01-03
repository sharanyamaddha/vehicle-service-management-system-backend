package com.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.model.enums.Role;
import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.responsedto.LoginResponse;
import com.userservice.service.UserService;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void shouldRegisterCustomer() throws Exception {

        RegisterRequest req = new RegisterRequest("cust","cust@test.com","123456",Role.CUSTOMER);

        when(userService.registerCustomer(any())).thenReturn("Registered Successfully");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registered Successfully"));
    }

    @Test
    public void shouldLogin() throws Exception {

        LoginRequest req = new LoginRequest("cust@test.com","123456");
        LoginResponse res = new LoginResponse("jwt",""cust","cust@test.com","CUSTOMER");

        when(userService.login(any())).thenReturn(res);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"));
    }
}
