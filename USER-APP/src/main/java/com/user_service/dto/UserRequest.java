package com.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "User name cannot be empty or blank.")
    @Size(min = 2, max = 50, message = "User name must be between 2 and 50 characters.")
    private String name;

    @NotBlank(message = "Email address is required.")
    @Email(message = "Please provide a valid email structure (e.g., user@example.com).")
    private String email;

    @NotBlank(message = "Shipping address is required to process orders.")
    @Size(max = 255, message = "Shipping address cannot exceed 255 characters.")
    private String shippingAddress;
}
