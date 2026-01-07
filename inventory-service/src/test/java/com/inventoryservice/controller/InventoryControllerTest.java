package com.inventoryservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import com.inventoryservice.model.RestockRequest;
import com.inventoryservice.requestdto.CreatePartRequest;
import com.inventoryservice.requestdto.CreateRestockRequest;
import com.inventoryservice.requestdto.UpdatePartRequest;
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

    private InventoryPart testPart;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        testPart = new InventoryPart();
        testPart.setId("p1");
        testPart.setName("Tire");
        testPart.setPrice(100.0);
        testPart.setStock(50);
        testPart.setReorderLevel(10);
        testPart.setCategory("Wheels");
        testPart.setDescription("Car Tire");
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

        when(inventoryService.addPart(any(CreatePartRequest.class))).thenReturn(testPart);

        mockMvc.perform(post("/api/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.name").value("Tire"));
    }

    @Test
    void getAllParts_Success() throws Exception {
        InventoryPart part2 = new InventoryPart();
        part2.setId("p2");
        part2.setName("Engine Oil");

        when(inventoryService.getAllParts()).thenReturn(List.of(testPart, part2));

        mockMvc.perform(get("/api/parts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Tire"))
                .andExpect(jsonPath("$[1].name").value("Engine Oil"));
    }

    @Test
    void getAllParts_Empty() throws Exception {
        when(inventoryService.getAllParts()).thenReturn(List.of());

        mockMvc.perform(get("/api/parts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getPartById_Success() throws Exception {
        when(inventoryService.getPartById("p1")).thenReturn(testPart);

        mockMvc.perform(get("/api/parts/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.name").value("Tire"))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    void updatePart_Success() throws Exception {
        UpdatePartRequest req = new UpdatePartRequest();
        req.setName("Updated Tire");
        req.setPrice(120.0);
        req.setReorderLevel(15);

        doNothing().when(inventoryService).updatePart(anyString(), any(UpdatePartRequest.class));

        mockMvc.perform(put("/api/parts/p1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Part updated successfully"));
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
    void deductStock_Multiple() throws Exception {
        UsedPartRequest req1 = new UsedPartRequest();
        req1.setPartId("p1");
        req1.setQty(5);

        UsedPartRequest req2 = new UsedPartRequest();
        req2.setPartId("p2");
        req2.setQty(3);

        doNothing().when(inventoryService).deductStock(any());

        mockMvc.perform(post("/api/parts/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(req1, req2))))
                .andExpect(status().isOk());
    }

    @Test
    void lowStockAlerts_Success() throws Exception {
        InventoryPart lowStockPart = new InventoryPart();
        lowStockPart.setId("p2");
        lowStockPart.setName("Engine Oil");
        lowStockPart.setStock(2);
        lowStockPart.setReorderLevel(5);

        when(inventoryService.getLowStockParts()).thenReturn(List.of(lowStockPart));

        mockMvc.perform(get("/api/parts/alerts/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Engine Oil"));
    }

    @Test
    void lowStockAlerts_None() throws Exception {
        when(inventoryService.getLowStockParts()).thenReturn(List.of());

        mockMvc.perform(get("/api/parts/alerts/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void requestRestock_Success() throws Exception {
        CreateRestockRequest req = new CreateRestockRequest();
        req.setPartId("p1");
        req.setQuantity(10);
        req.setManagerId("m1");
        req.setReason("Stock ran out");

        doNothing().when(inventoryService).requestRestock(any());

        mockMvc.perform(post("/api/parts/restock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Restock requested successfully"));
    }

    @Test
    void getPendingRestocks_Success() throws Exception {
        RestockRequest rr1 = new RestockRequest();
        rr1.setId("r1");
        rr1.setStatus("REQUESTED");

        RestockRequest rr2 = new RestockRequest();
        rr2.setId("r2");
        rr2.setStatus("REQUESTED");

        when(inventoryService.getPendingRestocks()).thenReturn(List.of(rr1, rr2));

        mockMvc.perform(get("/api/parts/restock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getPendingRestocks_Empty() throws Exception {
        when(inventoryService.getPendingRestocks()).thenReturn(List.of());

        mockMvc.perform(get("/api/parts/restock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void approveRestock_Success() throws Exception {
        doNothing().when(inventoryService).approveRestock("r1");

        mockMvc.perform(put("/api/parts/restock/r1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Restock approved and stock updated"));
    }
}
