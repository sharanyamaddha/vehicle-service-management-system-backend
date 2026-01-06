package com.inventoryservice.repository;

import com.inventoryservice.model.RestockRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RestockRequestRepository extends MongoRepository<RestockRequest, String> {
    List<RestockRequest> findByStatus(String status);
}
