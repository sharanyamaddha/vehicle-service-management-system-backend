package com.servicerequest.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.servicerequest.model.Invoice;


@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String>{

	List<Invoice> findByCustomerId(String customerId);

	boolean existsByServiceRequestId(String serviceRequestId);

}
