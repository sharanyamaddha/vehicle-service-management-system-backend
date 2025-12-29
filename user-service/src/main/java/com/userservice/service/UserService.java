package com.userservice.service;

import java.util.List;

import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.responsedto.LoginResponse;
import com.userservice.responsedto.UserResponse;

public interface UserService {

	String register(RegisterRequest request);
	
	 LoginResponse login(LoginRequest request);
	  
	 List<UserResponse> getAllUsers();

	    List<UserResponse> getPendingUsers();

	    String approveUser(String userId);

	    UserResponse getUserById(String userId);
	  
}
