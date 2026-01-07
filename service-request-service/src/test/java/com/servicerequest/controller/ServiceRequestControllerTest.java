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

    @Test
    void customerRequests_Success() throws Exception {
        when(service.getCustomerRequests("c1")).thenReturn(List.of());
        mockMvc.perform(get("/api/service-requests/customer/c1"))
                .andExpect(status().isOk());
    }

    @Test
    void startJob_Success() throws Exception {
        when(service.startJob("req1")).thenReturn("Job started");
        mockMvc.perform(patch("/api/service-requests/req1/start"))
                .andExpect(status().isOk());
    }

    @Test
    void requestParts_Success() throws Exception {
        java.util.List<com.servicerequest.model.UsedPart> parts = java.util.List.of();
        when(service.requestParts(anyString(), any())).thenReturn("Parts requested");

        mockMvc.perform(post("/api/service-requests/req1/parts/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(parts)))
                .andExpect(status().isOk());
    }

    @Test
    void approveParts_Success() throws Exception {
        when(service.approveParts("req1", "mgr1")).thenReturn("Approved");
        mockMvc.perform(patch("/api/service-requests/req1/parts/approve")
                .param("managerId", "mgr1"))
                .andExpect(status().isOk());
    }

    @Test
    void allRequests_Success() throws Exception {
        when(service.getAllRequests()).thenReturn(List.of());
        mockMvc.perform(get("/api/service-requests/manager"))
                .andExpect(status().isOk());
    }

    @Test
    void technicianRequests_Success() throws Exception {
        when(service.getTechnicianRequests("t1")).thenReturn(List.of());
        mockMvc.perform(get("/api/service-requests/technician/t1"))
                .andExpect(status().isOk());
    }

    @Test
    void closeRequest_Success() throws Exception {
        when(service.closeRequest("req1", 100.0)).thenReturn("Closed");
        mockMvc.perform(patch("/api/service-requests/req1/close")
                .param("laborCost", "100.0"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_Success() throws Exception {
        when(service.getById("req1")).thenReturn(new com.servicerequest.model.ServiceRequest());
        mockMvc.perform(get("/api/service-requests/req1"))
                .andExpect(status().isOk());
    }

    @Test
    void getTechnicianPerformance_Success() throws Exception {
        when(service.getTechnicianPerformance()).thenReturn(List.of());
        mockMvc.perform(get("/api/service-requests/technician-performance"))
                .andExpect(status().isOk());
    }

    @Test
    void getTechnicianWorkload_Success() throws Exception {
        when(service.getTechnicianWorkload()).thenReturn(java.util.Collections.emptyMap());
        mockMvc.perform(get("/api/service-requests/technician-workload"))
                .andExpect(status().isOk());
    }
}
