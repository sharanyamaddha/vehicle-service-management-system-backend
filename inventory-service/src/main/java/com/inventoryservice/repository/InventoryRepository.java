package com.inventoryservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.inventoryservice.model.InventoryPart;

@Repository
public interface InventoryRepository extends MongoRepository<InventoryPart,String>{

}
