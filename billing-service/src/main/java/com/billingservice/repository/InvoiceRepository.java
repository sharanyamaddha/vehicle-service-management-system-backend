package com.billingservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.billingservice.model.Invoice;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String>{

	List<Invoice> findByCustomerId(String customerId);

	boolean existsByServiceRequestId(String serviceRequestId);

}
