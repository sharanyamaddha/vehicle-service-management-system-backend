package com.inventoryservice.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import com.inventoryservice.requestdto.UpdatePartRequest;
import com.inventoryservice.requestdto.UsedPartRequest;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepo;

    @Mock
    private RestockRequestRepository restockRepo;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private InventoryPart samplePart;

    @BeforeEach
    void setUp() {
        samplePart = new InventoryPart();
        samplePart.setId("part1");
        samplePart.setName("Brake Pad");
        samplePart.setStock(10);
        samplePart.setReorderLevel(5);
        samplePart.setPrice(50.0);
    }

    @Test
    void addPart_Success() {
        CreatePartRequest req = new CreatePartRequest();
        req.setName("Oil Filter");
        req.setStock(20);
        req.setPrice(15.0);

        when(inventoryRepo.existsByName("Oil Filter")).thenReturn(false);
        when(inventoryRepo.save(any(InventoryPart.class))).thenAnswer(i -> i.getArguments()[0]);

        InventoryPart result = inventoryService.addPart(req);

        assertNotNull(result);
        assertEquals("Oil Filter", result.getName());
        verify(inventoryRepo).save(any(InventoryPart.class));
    }

    @Test
    void addPart_AlreadyExists() {
        CreatePartRequest req = new CreatePartRequest();
        req.setName("Brake Pad");

        when(inventoryRepo.existsByName("Brake Pad")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> inventoryService.addPart(req));
        verify(inventoryRepo, never()).save(any(InventoryPart.class));
    }

    @Test
    void getPartById_Success() {
        when(inventoryRepo.findById("part1")).thenReturn(Optional.of(samplePart));

        InventoryPart result = inventoryService.getPartById("part1");

        assertEquals("part1", result.getId());
    }

    @Test
    void getPartById_NotFound() {
        when(inventoryRepo.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventoryService.getPartById("unknown"));
    }

    @Test
    void updatePart_Success() {
        UpdatePartRequest req = new UpdatePartRequest();
        req.setName("Updated Pad");
        req.setPrice(60.0);
        req.setReorderLevel(5);

        when(inventoryRepo.findById("part1")).thenReturn(Optional.of(samplePart));
        when(inventoryRepo.save(any(InventoryPart.class))).thenReturn(samplePart);

        inventoryService.updatePart("part1", req);

        assertEquals("Updated Pad", samplePart.getName());
        assertEquals(60.0, samplePart.getPrice());
        verify(inventoryRepo).save(samplePart);
    }

    @Test
    void deductStock_Success() {
        UsedPartRequest req = new UsedPartRequest();
        req.setPartId("part1");
        req.setQty(2);

        when(inventoryRepo.findById("part1")).thenReturn(Optional.of(samplePart));

        inventoryService.deductStock(List.of(req));

        assertEquals(8, samplePart.getStock());
        verify(inventoryRepo).save(samplePart);
    }

    @Test
    void deductStock_InsufficientStock() {
        UsedPartRequest req = new UsedPartRequest();
        req.setPartId("part1");
        req.setQty(15); // > 10

        when(inventoryRepo.findById("part1")).thenReturn(Optional.of(samplePart));

        assertThrows(RuntimeException.class, () -> inventoryService.deductStock(List.of(req)));

        // Ensure stock was NOT changed
        assertEquals(10, samplePart.getStock());
        verify(inventoryRepo, never()).save(samplePart);
    }

    @Test
    void getLowStockParts_ReturnsBasedOnReorderLevel() {
        InventoryPart lowStockPart = new InventoryPart();
        lowStockPart.setStock(3);
        lowStockPart.setReorderLevel(5);

        InventoryPart healthyStockPart = new InventoryPart();
        healthyStockPart.setStock(10);
        healthyStockPart.setReorderLevel(5);

        when(inventoryRepo.findAll()).thenReturn(List.of(lowStockPart, healthyStockPart));

        List<InventoryPart> alerts = inventoryService.getLowStockParts();

        assertEquals(1, alerts.size());
        assertEquals(3, alerts.get(0).getStock());
    }

    @Test
    void requestRestock_Success() {
        com.inventoryservice.requestdto.CreateRestockRequest req = new com.inventoryservice.requestdto.CreateRestockRequest();
        req.setPartId("part1");
        req.setQuantity(50);
        req.setManagerId("mgr1");

        when(restockRepo.save(any(RestockRequest.class))).thenAnswer(i -> i.getArguments()[0]);

        inventoryService.requestRestock(req);

        verify(restockRepo).save(argThat(rr -> rr.getQuantity() == 50 && "REQUESTED".equals(rr.getStatus())));
    }

    @Test
    void approveRestock_Success() {
        RestockRequest rr = new RestockRequest();
        rr.setId("restock1");
        rr.setPartId("part1");
        rr.setQuantity(20);
        rr.setStatus("REQUESTED");

        when(restockRepo.findById("restock1")).thenReturn(Optional.of(rr));
        when(inventoryRepo.findById("part1")).thenReturn(Optional.of(samplePart));

        inventoryService.approveRestock("restock1");

        // Check stock updated
        assertEquals(30, samplePart.getStock()); // 10 + 20
        verify(inventoryRepo).save(samplePart);

        // Check request status updated
        assertEquals("APPROVED", rr.getStatus());
        verify(restockRepo).save(rr);
    }

    @Test
    void approveRestock_NotPending() {
        RestockRequest rr = new RestockRequest();
        rr.setId("restock1");
        rr.setStatus("APPROVED");

        when(restockRepo.findById("restock1")).thenReturn(Optional.of(rr));

        assertThrows(RuntimeException.class, () -> inventoryService.approveRestock("restock1"));
        verify(inventoryRepo, never()).save(any());
    }
}
