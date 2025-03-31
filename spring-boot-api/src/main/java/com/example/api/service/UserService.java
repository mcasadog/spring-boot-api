package com.example.api.service;

import com.example.api.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    
    List<UserDto> getAllUsers();
    
    Optional<UserDto> getUserById(Long id);
    
    Optional<UserDto> getUserByUsername(String username);
    
    Optional<UserDto> getUserByEmail(String email);
    
    UserDto createUser(UserDto userDto);
    
    UserDto updateUser(Long id, UserDto userDto);
    
    void deleteUser(Long id);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    void addRoleToUser(String username, String roleName);
    
    void removeRoleFromUser(String username, String roleName);
}
