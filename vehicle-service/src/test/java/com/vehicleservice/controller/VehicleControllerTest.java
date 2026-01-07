package com.vehicleservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.vehicleservice.requestdto.VehicleRequest;
import com.vehicleservice.responsedto.VehicleResponse;
import com.vehicleservice.service.VehicleService;

@ExtendWith(MockitoExtension.class)
public class VehicleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
    }

    @Test
    void create_Success() throws Exception {
        VehicleRequest req = new VehicleRequest();
        req.setOwnerId("user1");
        req.setRegistrationNumber("TS09EU1234"); // Matches Regex
        req.setMake("Toyota");
        req.setModel("Camry");
        req.setYear(2021);
        req.setColor("White");
        req.setType(com.vehicleservice.model.VehicleType.CAR);

        when(vehicleService.addVehicle(any(VehicleRequest.class))).thenReturn("Vehicle added");

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAll_Success() throws Exception {
        when(vehicleService.getAllVehicles()).thenReturn(List.of(new VehicleResponse()));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getByCustomer_Success() throws Exception {
        when(vehicleService.getVehiclesByCustomer("user1")).thenReturn(List.of(new VehicleResponse()));

        mockMvc.perform(get("/api/vehicles/customer/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void delete_Success() throws Exception {
        when(vehicleService.deleteVehicle("1")).thenReturn("Vehicle deleted");

        mockMvc.perform(delete("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Vehicle deleted"));
    }

    // Fixed Update Test to include all fields
    @Test
    void update_Success() throws Exception {
        VehicleRequest req = new VehicleRequest();
        req.setOwnerId("user1");
        req.setRegistrationNumber("TS09EU1234");
        req.setMake("Updated");
        req.setModel("Civic");
        req.setYear(2022);
        req.setColor("Red");
        req.setType(com.vehicleservice.model.VehicleType.CAR);

        doNothing().when(vehicleService).updateVehicle(anyString(), any(VehicleRequest.class));

        mockMvc.perform(put("/api/vehicles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Vehicle updated successfully"));
    }

    @Test
    void getById_Success() throws Exception {
        VehicleResponse res = new VehicleResponse();
        res.setId("1");
        res.setRegistrationNumber("AP01AB1234");
        when(vehicleService.getVehicleById("1")).thenReturn(res);

        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationNumber").value("AP01AB1234"));
    }

    @Test
    void checkRegistration_Success() throws Exception {
        when(vehicleService.existsByRegistrationNumber("AP01AB1234")).thenReturn(true);

        mockMvc.perform(get("/api/vehicles/check-registration/AP01AB1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
