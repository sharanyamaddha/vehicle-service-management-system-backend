package com.vehicleservice.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.vehicleservice.model.Vehicle;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {

	List<Vehicle> findByOwnerId(String Id);

	boolean existsByRegistrationNumber(String registrationNumber);
}
