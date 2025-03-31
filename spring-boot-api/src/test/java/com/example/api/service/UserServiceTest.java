package com.example.api.service;

import com.example.api.dto.UserDto;
import com.example.api.model.User;
import com.example.api.repository.UserRepository;
import com.example.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(true);
        user.setRoles(new HashSet<>());

        userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setEnabled(true);
        userDto.setRoles(new HashSet<>());
    }

    @Test
    void getUserByIdShouldReturnUserWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserDto> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByIdShouldReturnEmptyWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void createUserShouldEncodePasswordAndSaveUser() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void existsByUsernameShouldReturnTrueWhenUsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean result = userService.existsByUsername("testuser");

        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername("testuser");
    }

    @Test
    void existsByEmailShouldReturnTrueWhenEmailExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }
}
