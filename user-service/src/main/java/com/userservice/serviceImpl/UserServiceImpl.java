package com.userservice.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.userservice.exceptions.EmailAlreadyExistsException;
import com.userservice.exceptions.InvalidCredentialsException;
import com.userservice.model.Role;
import com.userservice.model.User;
import com.userservice.repository.UserRepository;
import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.responsedto.LoginResponse;
import com.userservice.service.UserService;

@Service
public class UserServiceImpl implements UserService{

    //private final PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

//    UserServiceImpl(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }
	
	@Override
	public String register(RegisterRequest req) {
		if(userRepository.findByEmail(req.getEmail()).isPresent())
				throw new EmailAlreadyExistsException("Email already exists");
		
		User user=new User();
		user.setUsername(req.getUsername());
	    user.setEmail(req.getEmail());
	    //user.setPassword(passwordEncoder.encode(req.getPassword()));
	    user.setRole(req.getRole());
	    
	    if(req.getRole() == Role.CUSTOMER){
	        user.setActive(true);   // customer auto-activated
	    } else {
	        user.setActive(false);  // technician / manager needs approval
	    }
	    userRepository.save(user);
	    
	    if(req.getRole() == Role.CUSTOMER)
	        return "Registration successful. You can login now.";
	    else
	        return "Registration successful. Pending admin approval.";
	}
	
	@Override
	public LoginResponse login(LoginRequest req) {

	    User user = userRepository.findByUsername(req.getUsername())
	            .orElseThrow(() -> new RuntimeException("Invalid username or password"));

//	    if(!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
//	        throw new InvalidCredentialsException("Invalid username or password");
//	    }

	    if(!user.isActive())
	        throw new RuntimeException("Account pending admin approval");

	    return new LoginResponse(user.getId(),user.getUsername(),user.getEmail(), user.getRole().name());
	}
	
	
	
	
}
