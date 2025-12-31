package com.inventoryservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventoryservice.model.InventoryPart;
import com.inventoryservice.repository.InventoryRepository;
import com.inventoryservice.requestdto.CreatePartRequest;
import com.inventoryservice.requestdto.UsedPartRequest;
import com.inventoryservice.service.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService{

	@Autowired
	private InventoryRepository inventoryRepo;
	
	@Override
	public InventoryPart addPart(CreatePartRequest req) {
		InventoryPart part=new InventoryPart();
		
		part.setName(req.getName());
		part.setPrice(req.getPrice());
		part.setReorderLevel(req.getReorderLevel());
		part.setStock(req.getStock());
		return inventoryRepo.save(part);
	}
	
    @Override
    public List<InventoryPart> getAllParts() {
        return inventoryRepo.findAll();
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

}
