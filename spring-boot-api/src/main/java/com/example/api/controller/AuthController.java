package com.example.api.controller;

import com.example.api.dto.AuthenticationRequest;
import com.example.api.dto.AuthenticationResponse;
import com.example.api.dto.UserDto;
import com.example.api.model.User;
import com.example.api.security.JwtService;
import com.example.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails);
        
        // Get user details for response
        UserDto userDto = userService.getUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token(jwtToken)
                .username(userDto.getUsername())
                .userId(userDto.getId())
                .email(userDto.getEmail())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody UserDto request
    ) {
        // Check if username or email already exists
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        // Create user
        UserDto createdUser = userService.createUser(request);
        
        // Generate token
        UserDetails userDetails = userService.loadUserByUsername(createdUser.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);
        
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token(jwtToken)
                .username(createdUser.getUsername())
                .userId(createdUser.getId())
                .email(createdUser.getEmail())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
