package com.inventoryservice.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventoryservice.model.InventoryPart;
import com.inventoryservice.model.RestockRequest;
import com.inventoryservice.repository.InventoryRepository;
import com.inventoryservice.repository.RestockRequestRepository;
import com.inventoryservice.requestdto.CreatePartRequest;
import com.inventoryservice.requestdto.CreateRestockRequest;

import com.inventoryservice.requestdto.UsedPartRequest;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepo;

    @Mock
    private RestockRequestRepository restockRepo;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private InventoryPart mockPart;

    @BeforeEach
    void setUp() {
        mockPart = new InventoryPart();
        mockPart.setId("p1");
        mockPart.setName("Tire");
        mockPart.setStock(10);
        mockPart.setReorderLevel(5);
        mockPart.setPrice(100.0);
    }

    @Test
    void addPart_Success() {
        CreatePartRequest req = new CreatePartRequest();
        req.setName("Tire");
        req.setStock(10);
        req.setReorderLevel(5);
        req.setPrice(100.0);

        when(inventoryRepo.existsByName(anyString())).thenReturn(false);
        when(inventoryRepo.save(any(InventoryPart.class))).thenReturn(mockPart);

        InventoryPart res = inventoryService.addPart(req);

        assertNotNull(res);
        assertEquals("Tire", res.getName());
    }

    @Test
    void addPart_Duplicate() {
        CreatePartRequest req = new CreatePartRequest();
        req.setName("Tire");

        when(inventoryRepo.existsByName("Tire")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> inventoryService.addPart(req));
    }

    @Test
    void deductStock_Success() {
        UsedPartRequest used = new UsedPartRequest();
        used.setPartId("p1");
        used.setQty(2);

        when(inventoryRepo.findById("p1")).thenReturn(Optional.of(mockPart));

        inventoryService.deductStock(List.of(used));

        assertEquals(8, mockPart.getStock());
        verify(inventoryRepo).save(mockPart);
    }

    @Test
    void deductStock_Insufficient_Fail() {
        UsedPartRequest used = new UsedPartRequest();
        used.setPartId("p1");
        used.setQty(20);

        when(inventoryRepo.findById("p1")).thenReturn(Optional.of(mockPart));

        assertThrows(RuntimeException.class, () -> inventoryService.deductStock(List.of(used)));
        assertEquals(10, mockPart.getStock()); // Unchanged
    }

    @Test
    void requestRestock_Success() {
        CreateRestockRequest req = new CreateRestockRequest();
        req.setPartId("p1");
        req.setQuantity(50);
        req.setManagerId("m1");

        when(restockRepo.save(any(RestockRequest.class))).thenReturn(new RestockRequest());

        inventoryService.requestRestock(req);

        verify(restockRepo).save(any(RestockRequest.class));
    }

    @Test
    void approveRestock_Success() {
        RestockRequest rr = new RestockRequest();
        rr.setId("r1");
        rr.setPartId("p1");
        rr.setQuantity(50);
        rr.setStatus("REQUESTED");

        when(restockRepo.findById("r1")).thenReturn(Optional.of(rr));
        when(inventoryRepo.findById("p1")).thenReturn(Optional.of(mockPart));

        inventoryService.approveRestock("r1");

        assertEquals(60, mockPart.getStock());
        assertEquals("APPROVED", rr.getStatus());
        verify(inventoryRepo).save(mockPart);
        verify(restockRepo).save(rr);
    }
}
