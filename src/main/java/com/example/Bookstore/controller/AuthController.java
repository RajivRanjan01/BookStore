package com.example.Bookstore.controller;

import com.example.Bookstore.dto.LoginRequest;
import com.example.Bookstore.dto.SignupRequest;
import com.example.Bookstore.dto.JwtResponse;
import com.example.Bookstore.model.User;
import com.example.Bookstore.repository.UserRepository;
import com.example.Bookstore.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.generateToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}