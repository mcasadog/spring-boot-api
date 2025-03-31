package com.example.api.service.impl;

import com.example.api.dto.UserDto;
import com.example.api.model.Role;
import com.example.api.model.User;
import com.example.api.repository.RoleRepository;
import com.example.api.repository.UserRepository;
import com.example.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getCredentialsNonExpired(),
                user.getAccountNonLocked(),
                authorities
        );
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::mapToDto);
    }

    @Override
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::mapToDto);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapToEntity(userDto);
        
        // Encode password
        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        // Set default values
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Add roles if specified
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            userDto.getRoles().forEach(roleName -> {
                addRoleToUser(savedUser.getUsername(), roleName);
            });
        }
        
        return mapToDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Update fields
        if (userDto.getUsername() != null) {
            existingUser.setUsername(userDto.getUsername());
        }
        
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }
        
        if (userDto.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        if (userDto.getFirstName() != null) {
            existingUser.setFirstName(userDto.getFirstName());
        }
        
        if (userDto.getLastName() != null) {
            existingUser.setLastName(userDto.getLastName());
        }
        
        if (userDto.getEnabled() != null) {
            existingUser.setEnabled(userDto.getEnabled());
        }
        
        // Save updated user
        User updatedUser = userRepository.save(existingUser);
        
        return mapToDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));
        
        user.getRoles().add(role);
    }

    @Override
    public void removeRoleFromUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));
        
        user.getRoles().remove(role);
    }
    
    // Helper methods for mapping between entity and DTO
    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.getEnabled())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }
    
    private User mapToEntity(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEnabled(userDto.getEnabled());
        return user;
    }
}
