package com.userservice.service;

import java.util.List;

import com.userservice.requestdto.ChangePasswordRequest;
import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.requestdto.UpdateProfileRequest;
import com.userservice.responsedto.LoginResponse;
import com.userservice.responsedto.UserResponse;

public interface UserService {

    String registerCustomer(RegisterRequest req);

    String createInternalUser(RegisterRequest req);

    LoginResponse login(LoginRequest req);
    
    UserResponse getMyProfile(String token);
    
    void updateMyProfile(String token, UpdateProfileRequest req);
    
    void changeMyPassword(String token, ChangePasswordRequest req);


    List<UserResponse> getAllUsers();
    
    UserResponse getUserById(String id);

    
    List<UserResponse> getUsersByRole(String role);

    void disableUser(String id);

    void enableUser(String id);
    
    List<UserResponse> getDisabledUsers();

    void resetPassword(String id);

    
}
