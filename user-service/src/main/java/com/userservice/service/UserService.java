package com.userservice.service;

import com.userservice.requestdto.RegisterRequest;

public interface UserService {

	String register(RegisterRequest request);
}
