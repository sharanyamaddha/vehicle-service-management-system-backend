package com.servicerequest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

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
import com.servicerequest.model.ServiceBay;
import com.servicerequest.service.ServiceBayService;

@ExtendWith(MockitoExtension.class)
public class ServiceBayControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ServiceBayService bayService;

    @InjectMocks
    private ServiceBayController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void create_Success() throws Exception {
        ServiceBay bay = new ServiceBay();
        bay.setBayNumber(1);

        when(bayService.createBay(any(ServiceBay.class))).thenReturn(bay);

        mockMvc.perform(post("/api/bays")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bay)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bayNumber").value(1));
    }

    @Test
    void available_Success() throws Exception {
        when(bayService.getAvailableBays()).thenReturn(List.of(new ServiceBay()));

        mockMvc.perform(get("/api/bays/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAll_Success() throws Exception {
        when(bayService.getAllBays()).thenReturn(List.of(new ServiceBay()));

        mockMvc.perform(get("/api/bays"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void updateStatus_Success() throws Exception {
        Map<String, Boolean> statusMap = Map.of("isAvailable", true); // Note: Controller expects "isAvailable" map for
                                                                      // active/inactive logic is slightly weird name in
                                                                      // controller code but sticking to it

        doNothing().when(bayService).updateStatus(anyInt(), anyBoolean());

        mockMvc.perform(patch("/api/bays/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Status updated")); // Controller returns String body
    }

    @Test
    void releaseBay_Success() throws Exception {
        doNothing().when(bayService).releaseBay(1);

        mockMvc.perform(post("/api/bays/1/release"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Bay released manually"));
    }
}
