package com.servicerequest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.servicerequest.model.ServiceBay;

@Repository
public interface ServiceBayRepository extends MongoRepository<ServiceBay, String>{

	List<ServiceBay> findByAvailableTrue();

	Optional<ServiceBay> findByBayNumber(int bayNumber);

}
