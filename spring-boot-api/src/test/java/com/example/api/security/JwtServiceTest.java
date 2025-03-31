package com.example.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private String token;

    @BeforeEach
    void setUp() {
        // Set the secret key and expiration via reflection
        try {
            java.lang.reflect.Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
            secretKeyField.setAccessible(true);
            secretKeyField.set(jwtService, "veryLongAndSecureSecretKeyForJwtSigningThatShouldBeAtLeast256BitsLong");
            
            java.lang.reflect.Field jwtExpirationField = JwtService.class.getDeclaredField("jwtExpiration");
            jwtExpirationField.setAccessible(true);
            jwtExpirationField.set(jwtService, 86400000L);
            
            java.lang.reflect.Field issuerField = JwtService.class.getDeclaredField("issuer");
            issuerField.setAccessible(true);
            issuerField.set(jwtService, "spring-boot-api");
        } catch (Exception e) {
            e.printStackTrace();
        }

        userDetails = new User("testuser", "password", new ArrayList<>());
        token = jwtService.generateToken(userDetails);
    }

    @Test
    void extractUsernameShouldReturnCorrectUsername() {
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void isTokenValidShouldReturnTrueForValidToken() {
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void isTokenValidShouldReturnFalseForInvalidUser() {
        UserDetails otherUser = new User("otheruser", "password", new ArrayList<>());
        boolean isValid = jwtService.isTokenValid(token, otherUser);
        assertFalse(isValid);
    }
}
