package com.servicerequest.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.servicerequest.enums.ServiceStatus;
import com.servicerequest.model.ServiceRequest;

@Repository
public interface ServiceRequestRepository extends MongoRepository<ServiceRequest,String>{

	List<ServiceRequest> findByCustomerId(String customerId);

	List<ServiceRequest> findByStatus(ServiceStatus status);

	List<ServiceRequest> findByTechnicianId(String technicianId);

}
