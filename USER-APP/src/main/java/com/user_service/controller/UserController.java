package com.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.user_service.dto.StandardResponse;
import com.user_service.dto.UserRequest;
import com.user_service.dto.UserResponse;
import com.user_service.dto.UserUpdateRequest;
import com.user_service.service.UserService;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<StandardResponse<UserResponse>> createUser(@RequestBody @Valid UserRequest request) {
        UserResponse savedUser = userService.createUser(request);
        StandardResponse<UserResponse> response = StandardResponse.<UserResponse>builder()
                .success(true)
                .message("User profile provisioned successfully.")
                .data(savedUser)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<UserResponse>> getUser(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        StandardResponse<UserResponse> response = StandardResponse.<UserResponse>builder()
                .success(true)
                .message("User profile data fetched successfully.")
                .data(user)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    // Endpoint: Fetch a specific user by email address string
    @GetMapping("/email/{email}")
    public ResponseEntity<StandardResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserByEmail(email);
        StandardResponse<UserResponse> response = StandardResponse.<UserResponse>builder()
                .success(true)
                .message("User profile matching email retrieved successfully.")
                .data(user)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<StandardResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        StandardResponse<List<UserResponse>> response = StandardResponse.<List<UserResponse>>builder()
                .success(true)
                .message("All user profiles retrieved successfully.")
                .data(users)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<UserResponse>> updateUser(
        @PathVariable Long id, 
        @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse updatedUser = userService.updateExistingUser(id, request);
        StandardResponse<UserResponse> response = StandardResponse.<UserResponse>builder()
                .success(true)
                .message("User profile matching email retrieved successfully.")
                .data(updatedUser)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
