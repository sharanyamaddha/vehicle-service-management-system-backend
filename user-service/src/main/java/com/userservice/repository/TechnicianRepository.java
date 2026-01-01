package com.userservice.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.userservice.model.Technician;
import com.userservice.responsedto.TechnicianResponse;

@Repository
public interface TechnicianRepository extends MongoRepository<Technician,String>{

	List<Technician> findByAvailableTrue();
    
    List<Technician> findByAvailable(boolean status);
    
    List<Technician> findBySpecializationAndAvailable(String specialization, boolean available);


	
}
