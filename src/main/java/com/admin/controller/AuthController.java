package com.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.admin.dto.LoginRequest;
import com.admin.dto.RegisterRequest;
import com.admin.entity.User;
import com.admin.exception.EmailAlreadyExistsException;
import com.admin.repo.UserRepository;
import com.admin.security.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
           ) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
      
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
    		@Valid  @RequestBody RegisterRequest request) {

    	if (userRepository.findByEmail(request.getEmail()).isPresent()) {
    	    throw new EmailAlreadyExistsException(
    	            "Email already registered"
    	    );
    	}

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // IMPORTANT: user registration always creates USER
        user.setRole("USER");

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return jwtService.generateToken(request.getEmail());
    }
    
    

    
    
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout() {
//        return ResponseEntity.ok("Logged out successfully");
//    }
//    
    
//  @PostMapping("/logout")
//  public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
//      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//          return ResponseEntity.badRequest().body("Bearer token is required");
//      }
//      String token = authHeader.substring(7);
//
//      Instant expiry;
//      try {
//          expiry = jwtService.extractExpiration(token);
//      } catch (Exception e) {
//          // Token is malformed/already expired — nothing to blacklist
//          return ResponseEntity.ok("Logged out successfully");
//      }
//
//      tokenBlacklistService.blacklist(token, expiry);
//      return ResponseEntity.ok("Logged out successfully");
//  }
//  
}