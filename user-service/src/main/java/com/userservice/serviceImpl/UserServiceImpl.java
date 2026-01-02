package com.userservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.userservice.exceptions.EmailAlreadyExistsException;
import com.userservice.exceptions.InvalidCredentialsException;
import com.userservice.model.User;
import com.userservice.model.enums.Role;
import com.userservice.repository.UserRepository;
import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.responsedto.LoginResponse;
import com.userservice.responsedto.UserResponse;
import com.userservice.security.JwtUtil;
import com.userservice.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @Override
    public String registerCustomer(RegisterRequest req){

        if(userRepository.findByEmail(req.getEmail()).isPresent())
            throw new EmailAlreadyExistsException("Email already exists");

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setActive(true);

        userRepository.save(user);
        return "Customer registered successfully";
    }

    @Override
    public String createInternalUser(RegisterRequest req){

        if(req.getRole() == Role.CUSTOMER)
            throw new RuntimeException("Use public registration for customers");

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setActive(true);

        userRepository.save(user);
        return "Internal user created successfully";
    }

    @Override
    public LoginResponse login(LoginRequest req){

        User user = userRepository.findByUsername(req.getUsername())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if(!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException("Invalid credentials");

        if(!user.isActive())
            throw new RuntimeException("Account disabled");

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token,user.getId(),user.getUsername(),user.getEmail(),user.getRole().name());
    }

    @Override
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll().stream().map(this::map).toList();
    }
    
    @Override
    public UserResponse getUserById(String id) {

        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return map(user);
    }

    
    @Override
    public List<UserResponse> getUsersByRole(String role) {
        return userRepository.findByRole(Role.valueOf(role))
                .stream().map(this::map).toList();
    }


    @Override
    public void disableUser(String id){
        User u = userRepository.findById(id).orElseThrow();
        u.setActive(false);
        userRepository.save(u);
    }

    @Override
    public void enableUser(String id){
        User u = userRepository.findById(id).orElseThrow();
        u.setActive(true);
        userRepository.save(u);
    }

    private UserResponse map(User u){
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setRole(u.getRole());
        r.setActive(u.isActive());
        return r;
    }
}
