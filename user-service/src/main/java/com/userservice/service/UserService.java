package com.userservice.service;

import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.responsedto.LoginResponse;

public interface UserService {

	String register(RegisterRequest request);
	
	  LoginResponse login(LoginRequest request);
}
