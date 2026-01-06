package com.servicerequest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.servicerequest.requestdto.AssignTechnicianDTO;
import com.servicerequest.requestdto.ServiceRequestDTO;
import com.servicerequest.requestdto.UpdateStatusDTO;
import com.servicerequest.service.ServiceRequestService;
import com.servicerequest.enums.ServiceStatus;

@ExtendWith(MockitoExtension.class)
public class ServiceRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ServiceRequestService service;

    @InjectMocks
    private ServiceRequestController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void create_Success() throws Exception {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setVehicleId("v1");
        dto.setCustomerId("c1");
        dto.setIssue("Issue");
        dto.setPriority(com.servicerequest.enums.Priority.HIGH);

        when(service.createRequest(any(ServiceRequestDTO.class))).thenReturn("Created");

        mockMvc.perform(post("/api/service-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void assign_Success() throws Exception {
        AssignTechnicianDTO dto = new AssignTechnicianDTO();
        dto.setTechnicianId("t1");
        dto.setBayNumber(1);

        when(service.assignTechnician(anyString(), any(AssignTechnicianDTO.class))).thenReturn("Assigned");

        mockMvc.perform(patch("/api/service-requests/req1/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_Success() throws Exception {
        UpdateStatusDTO dto = new UpdateStatusDTO();
        dto.setStatus(ServiceStatus.COMPLETED);

        when(service.updateStatus(anyString(), any(UpdateStatusDTO.class))).thenReturn("Updated");

        mockMvc.perform(patch("/api/service-requests/req1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getByStatus_Success() throws Exception {
        when(service.getByStatus(ServiceStatus.REQUESTED)).thenReturn(List.of());

        mockMvc.perform(get("/api/service-requests/status/REQUESTED"))
                .andExpect(status().isOk());
    }
}
