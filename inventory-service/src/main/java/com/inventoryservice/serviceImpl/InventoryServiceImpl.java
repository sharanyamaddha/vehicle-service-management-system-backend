package com.inventoryservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventoryservice.model.InventoryPart;
import com.inventoryservice.repository.InventoryRepository;
import com.inventoryservice.requestdto.CreatePartRequest;
import com.inventoryservice.requestdto.UpdatePartRequest;
import com.inventoryservice.requestdto.UsedPartRequest;
import com.inventoryservice.service.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepo;

    @Override
    public InventoryPart addPart(CreatePartRequest req) {
        if (inventoryRepo.existsByName(req.getName())) {
            throw new RuntimeException("Part with name '" + req.getName() + "' already exists.");
        }

        InventoryPart part = new InventoryPart();

        part.setName(req.getName());
        part.setPrice(req.getPrice());
        part.setReorderLevel(req.getReorderLevel());
        part.setStock(req.getStock());
        part.setCategory(req.getCategory());
        part.setDescription(req.getDescription());
        return inventoryRepo.save(part);
    }

    @Override
    public List<InventoryPart> getAllParts() {
        return inventoryRepo.findAll();
    }

    @Override
    public InventoryPart getPartById(String id) {
        return inventoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Part not found: " + id));
    }

    @Override
    public void updatePart(String id, UpdatePartRequest req) {

        InventoryPart part = inventoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        part.setName(req.getName());
        part.setPrice(req.getPrice());
        part.setReorderLevel(req.getReorderLevel());

        inventoryRepo.save(part);
    }

    @Transactional
    @Override
    public void deductStock(List<UsedPartRequest> usedParts) {

        for (UsedPartRequest used : usedParts) {

            InventoryPart part = inventoryRepo.findById(used.getPartId())
                    .orElseThrow(() -> new RuntimeException("Part not found: " + used.getPartId()));

            if (part.getStock() < used.getQty()) {
                throw new RuntimeException("Insufficient stock for part: " + part.getName());
            }

            part.setStock(part.getStock() - used.getQty());
            inventoryRepo.save(part);
        }
    }

    @Override
    public List<InventoryPart> getLowStockParts() {
        return inventoryRepo.findAll()
                .stream()
                .filter(p -> p.getStock() <= p.getReorderLevel())
                .toList();
    }

    @Autowired
    private com.inventoryservice.repository.RestockRequestRepository restockRepo;

    @Override
    public void requestRestock(com.inventoryservice.requestdto.CreateRestockRequest req) {
        com.inventoryservice.model.RestockRequest rr = new com.inventoryservice.model.RestockRequest();
        rr.setPartId(req.getPartId());
        rr.setQuantity(req.getQuantity());
        rr.setManagerId(req.getManagerId());
        rr.setReason(req.getReason());
        rr.setStatus("REQUESTED");
        rr.setRequestedAt(java.time.LocalDateTime.now());
        restockRepo.save(rr);
    }

    @Override
    public List<com.inventoryservice.model.RestockRequest> getPendingRestocks() {
        return restockRepo.findByStatus("REQUESTED");
    }

    @Transactional
    @Override
    public void approveRestock(String id) {
        com.inventoryservice.model.RestockRequest rr = restockRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Restock request not found"));

        if (!"REQUESTED".equals(rr.getStatus())) {
            throw new RuntimeException("Request is not in pending state");
        }

        InventoryPart part = inventoryRepo.findById(rr.getPartId())
                .orElseThrow(() -> new RuntimeException("Part not found"));

        part.setStock(part.getStock() + rr.getQuantity());
        inventoryRepo.save(part);

        rr.setStatus("APPROVED");
        rr.setResolvedAt(java.time.LocalDateTime.now());
        restockRepo.save(rr);
    }

}
