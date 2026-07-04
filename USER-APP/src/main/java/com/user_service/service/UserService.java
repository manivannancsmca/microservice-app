package com.user_service.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user_service.dto.UserRequest;
import com.user_service.dto.UserResponse;
import com.user_service.dto.UserUpdateRequest;
import com.user_service.entity.User;
import com.user_service.exception.ResourceNotFoundException;
import com.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
// 1. Establish a safe-by-default read-only boundary across the service to optimize connection pooling
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User profile matching identity ID " + id + " does not exist."));
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No user profile found matching email address: " + email));
        return mapToResponse(user);
    }

    // 2. Open a read-write transaction explicitly. Roll back on any checked or unchecked exception.
    @Transactional(rollbackFor = Exception.class)
    public UserResponse createUser(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setShippingAddress(request.getShippingAddress());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    // 3. Override default read-only strategy for mutations and ensure proper exception mapping
    @Transactional(rollbackFor = Exception.class)
    public UserResponse updateExistingUser(Long id, UserUpdateRequest request) {
        // Fixed: Use ResourceNotFoundException instead of generic RuntimeException
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setName(request.name());
        user.setShippingAddress(request.shippingAddress());
        user.setEmail(request.email());

        // Note: Generally metadata like createdAt shouldn't be mutable via requests, 
        // but if required, handled under transaction block safely.
        if (request.createdAt() != null) {
            user.setCreatedAt(request.createdAt());
        }

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .shippingAddress(user.getShippingAddress())
                .build();
    }
}