package com.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.userservice.model.enums.Specialization;
import com.userservice.requestdto.TechnicianCreateRequest;
import com.userservice.responsedto.TechnicianResponse;
import com.userservice.service.TechnicianService;

@ExtendWith(MockitoExtension.class)
public class TechnicianControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TechnicianService techService;

    @InjectMocks
    private TechnicianController techController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(techController).build();
    }

    @Test
    void createTechnician_Success() throws Exception {
        TechnicianCreateRequest req = new TechnicianCreateRequest();
        req.setUserId("user1");
        req.setSpecialization(Specialization.BODYWORK);

        TechnicianResponse res = new TechnicianResponse();
        res.setUserId("user1");
        res.setSpecialization(Specialization.BODYWORK);

        when(techService.createTechnician(any(TechnicianCreateRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/technicians")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user1"));
    }

    @Test
    void byStatus_Success() throws Exception {
        when(techService.getTechniciansByStatus(true)).thenReturn(List.of(new TechnicianResponse()));

        mockMvc.perform(get("/api/technicians/status/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void incrementWorkload_Success() throws Exception {
        doNothing().when(techService).incrementWorkload("tech1");

        mockMvc.perform(put("/api/technicians/tech1/increment"))
                .andExpect(status().isOk());
    }
}
