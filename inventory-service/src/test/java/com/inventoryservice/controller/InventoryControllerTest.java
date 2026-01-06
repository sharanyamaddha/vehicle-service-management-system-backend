package com.inventoryservice.controller;

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
import com.inventoryservice.model.InventoryPart;
import com.inventoryservice.requestdto.CreatePartRequest;
import com.inventoryservice.requestdto.CreateRestockRequest;
import com.inventoryservice.requestdto.UsedPartRequest;
import com.inventoryservice.service.InventoryService;

@ExtendWith(MockitoExtension.class)
public class InventoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void addPart_Success() throws Exception {
        CreatePartRequest req = new CreatePartRequest();
        req.setName("Part1");
        req.setPrice(10.0);
        req.setStock(50);
        req.setReorderLevel(10);
        req.setCategory("Cat");
        req.setDescription("Desc");

        when(inventoryService.addPart(any(CreatePartRequest.class))).thenReturn(new InventoryPart());

        mockMvc.perform(post("/api/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllParts_Success() throws Exception {
        when(inventoryService.getAllParts()).thenReturn(List.of(new InventoryPart()));

        mockMvc.perform(get("/api/parts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deductStock_Success() throws Exception {
        UsedPartRequest req = new UsedPartRequest();
        req.setPartId("p1");
        req.setQty(5);

        doNothing().when(inventoryService).deductStock(any());

        mockMvc.perform(post("/api/parts/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(req))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Stock updated successfully"));
    }

    @Test
    void restock_Success() throws Exception {
        CreateRestockRequest req = new CreateRestockRequest();
        req.setPartId("p1");
        req.setQuantity(10);
        req.setManagerId("m1");

        doNothing().when(inventoryService).requestRestock(any());

        mockMvc.perform(post("/api/parts/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void approveRestock_Success() throws Exception {
        doNothing().when(inventoryService).approveRestock("r1");

        mockMvc.perform(put("/api/parts/restock/r1/approve"))
                .andExpect(status().isOk());
    }
}
