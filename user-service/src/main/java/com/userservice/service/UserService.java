package com.userservice.service;

import java.util.List;

import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.responsedto.LoginResponse;
import com.userservice.responsedto.UserResponse;

public interface UserService {

    String registerCustomer(RegisterRequest req);

    String createInternalUser(RegisterRequest req);

    LoginResponse login(LoginRequest req);

    List<UserResponse> getAllUsers();
    
    List<UserResponse> getUsersByRole(String role);

    void disableUser(String id);

    void enableUser(String id);
}
