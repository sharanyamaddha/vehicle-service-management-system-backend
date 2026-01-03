package com.inventoryservice.service;

import java.util.List;

import com.inventoryservice.model.InventoryPart;
import com.inventoryservice.requestdto.CreatePartRequest;
import com.inventoryservice.requestdto.UpdatePartRequest;
import com.inventoryservice.requestdto.UsedPartRequest;

public interface InventoryService {

    InventoryPart addPart(CreatePartRequest request);

    List<InventoryPart> getAllParts();
    
    void updatePart(String id, UpdatePartRequest req);

    
    InventoryPart getPartById(String id);


    void deductStock(List<UsedPartRequest> usedParts);
    

	List<InventoryPart> getLowStockParts();
}
