package com.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record UserUpdateRequest(
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @Size(max = 255, message = "Shipping address cannot exceed 255 characters")
    String shippingAddress,

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    String email,

    // Included for our auditing protection test simulation
    Instant createdAt 
) {}
