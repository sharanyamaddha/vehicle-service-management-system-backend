package com.userservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.userservice.model.Manager;

@Repository
public interface ManagerRepository extends MongoRepository<Manager,String>{

}
