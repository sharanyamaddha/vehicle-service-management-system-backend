package com.userservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.userservice.model.User;
import com.userservice.model.enums.Role;

@Repository
public interface UserRepository extends MongoRepository<User,String>{

	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	Optional<User> findByActiveFalse();

	Optional<User> findByRole(Role valueOf);


}
