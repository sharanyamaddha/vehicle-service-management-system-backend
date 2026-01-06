package com.servicerequest;

import java.util.List;

import org.springframework.stereotype.Component;

import com.servicerequest.FeignClient.InventoryClient;
import com.servicerequest.model.UsedPart;
import com.servicerequest.requestdto.InventoryPartDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class InventoryClientFallback implements InventoryClient {

    @Override
    public void deductStock(List<UsedPart> parts) {

        log.error("CIRCUIT BREAKER: Failed to deduct stock for parts: {}. Inventory Service is down.", parts);
    }

    @Override
    public InventoryPartDTO getPart(String id) {
        log.error("CIRCUIT BREAKER: Failed to get part details for ID: {}. Inventory Service is down.", id);
        InventoryPartDTO fallbackDTO = new InventoryPartDTO();
        fallbackDTO.setId(id);
        fallbackDTO.setPrice(0.0);
        fallbackDTO.setFallback(true);
        return fallbackDTO;
    }
}