package com.userservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.userservice.responsedto.UserResponse;
import com.userservice.requestdto.ChangePasswordRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.requestdto.UpdateProfileRequest;
import com.userservice.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	// ADMIN – view all users
	@GetMapping
	public ResponseEntity<List<UserResponse>> all() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	@GetMapping("/role/{role}")
	public ResponseEntity<List<UserResponse>> byRole(@PathVariable String role) {
		return ResponseEntity.ok(userService.getUsersByRole(role));
	}

	// ADMIN – create MANAGER / TECHNICIAN
	@PostMapping("/create")
	public ResponseEntity<Map<String, String>> createInternal(@RequestBody RegisterRequest req) {
		return ResponseEntity.ok(Map.of("message", userService.createInternalUser(req)));
	}

	// ADMIN – disable user
	@PatchMapping("/{id}/disable")
	public ResponseEntity<Map<String, String>> disable(@PathVariable String id) {
		userService.disableUser(id);
		return ResponseEntity.ok(Map.of("message", "User disabled"));
	}

	// ADMIN – enable user
	@PatchMapping("/{id}/enable")
	public ResponseEntity<Map<String, String>> enable(@PathVariable String id) {
		userService.enableUser(id);
		return ResponseEntity.ok(Map.of("message", "User enabled"));
	}

	@GetMapping("/disabled")
	public ResponseEntity<List<UserResponse>> disabledUsers() {
		return ResponseEntity.ok(userService.getDisabledUsers());
	}

	@GetMapping("/me")
	public ResponseEntity<UserResponse> me(@RequestHeader("Authorization") String token) {
		return ResponseEntity.ok(userService.getMyProfile(token));
	}

	@PutMapping("/me")
	public ResponseEntity<Map<String, String>> updateProfile(@RequestHeader("Authorization") String auth,
			@Valid	@RequestBody UpdateProfileRequest req) {
		userService.updateMyProfile(auth, req);
		return ResponseEntity.ok(Map.of("message", "Profile updated"));
	}

	@PatchMapping("/me/password")
	public ResponseEntity<Map<String, String>> changePassword(@RequestHeader("Authorization") String auth,
			@Valid	@RequestBody ChangePasswordRequest req) {
		userService.changeMyPassword(auth, req);
		return ResponseEntity.ok(Map.of("message", "Password updated"));
	}

	@PatchMapping("/{id}/reset-password")
	public ResponseEntity<Map<String,String>> resetPassword(@PathVariable String id){
	    userService.resetPassword(id);
	    return ResponseEntity.ok(Map.of("message","Password reset to default"));
	}

}
