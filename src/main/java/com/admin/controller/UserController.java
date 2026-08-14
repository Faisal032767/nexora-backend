package com.admin.controller;

import com.admin.dto.CreateUserRequest;
import com.admin.dto.UpdateProfileRequest;
import com.admin.dto.UserResponse;
import com.admin.entity.User;
import com.admin.exception.UserNotFoundException;
import com.admin.repo.UserRepository;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping
	public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			return ResponseEntity.status(409).body("Email already registered");
		}

		User user = new User();

		user.setName(request.getName());
		user.setEmail(request.getEmail());

		user.setPassword(passwordEncoder.encode(request.getPassword()));

		user.setRole(request.getRole());

		userRepository.save(user);

		return ResponseEntity.ok("User created successfully");
	}

	@GetMapping("/me")
	public ResponseEntity<?> getMyProfile(Authentication authentication) {

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
		UserResponse response = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
		return ResponseEntity.ok(response);
	}

	@PutMapping("/me")
	public ResponseEntity<?> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request,
			Authentication authentication) {

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

		if (request.getName() != null && !request.getName().isBlank()) {

			user.setName(request.getName());
		}

		if (request.getPassword() != null && !request.getPassword().isBlank()) {

			user.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		userRepository.save(user);

		UserResponse response = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers() {

		List<UserResponse> users = userRepository.findAll().stream()
				.map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole())).toList();

		return ResponseEntity.ok(users);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {

		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

		userRepository.delete(user);

		return ResponseEntity.ok("User deleted successfully");
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(
	        @PathVariable Long id,
	        @Valid @RequestBody CreateUserRequest request) {

	    User user = userRepository.findById(id)
	            .orElseThrow(() ->
	                    new UserNotFoundException("User not found"));

	    user.setName(request.getName());
	    user.setEmail(request.getEmail());
	    user.setRole(request.getRole());

	    // Only update password if a new password is provided
	    if (request.getPassword() != null &&
	            !request.getPassword().isBlank()) {

	        user.setPassword(
	                passwordEncoder.encode(request.getPassword())
	        );
	    }

	    userRepository.save(user);

	    UserResponse response = new UserResponse(
	            user.getId(),
	            user.getName(),
	            user.getEmail(),
	            user.getRole()
	    );

	    return ResponseEntity.ok(response);
	}

}