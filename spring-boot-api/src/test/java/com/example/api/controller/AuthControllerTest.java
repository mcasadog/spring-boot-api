package com.example.api.controller;

import com.example.api.dto.AuthenticationRequest;
import com.example.api.dto.AuthenticationResponse;
import com.example.api.dto.UserDto;
import com.example.api.security.JwtService;
import com.example.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private AuthenticationRequest authRequest;
    private UserDto userDto;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        authRequest = new AuthenticationRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");

        userDetails = new User("testuser", "password", new ArrayList<>());
    }

    @Test
    void authenticateShouldReturnTokenWhenCredentialsAreValid() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(userDto));

        ResponseEntity<AuthenticationResponse> response = authController.authenticate(authRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().getToken());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals(1L, response.getBody().getUserId());
    }

    @Test
    void registerShouldReturnCreatedUserWithToken() {
        when(userService.existsByUsername(anyString())).thenReturn(false);
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);
        when(userService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        ResponseEntity<AuthenticationResponse> response = authController.register(userDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().getToken());
        assertEquals("testuser", response.getBody().getUsername());
    }
}
