package com.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.user_service.dto.StandardResponse;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handles Spring Data validation errors (@Valid failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StandardResponse<Map<String, String>> response = StandardResponse.<Map<String, String>>builder()
                .success(false)
                .message("Request payload validation failed.")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 2. Handles Missing Profiles/Items (Your Custom Exception)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        StandardResponse<Void> response = StandardResponse.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 3. NEW: Handles SQL Duplicate Constraints (e.g., Unique Email Violations)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Log the actual error internally for debugging
        String errorMessage = "A user profile with this email address already exists. Please use a unique email address.";

        StandardResponse<Void> response = StandardResponse.<Void>builder()
                .success(false)
                .message(errorMessage)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.CONFLICT); // Returns standard HTTP 409 Conflict status
    }

    // 4. NEW: Handles Bad Data Types in URL (e.g., calling /api/users/abc instead of an ID number)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardResponse<String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String detailMessage = String.format("Parameter '%s' expected a type of '%s', but received value '%s'.", 
                ex.getName(), ex.getRequiredType().getSimpleName(), ex.getValue());
        
        StandardResponse<String> response = StandardResponse.<String>builder()
                .success(false)
                .message("Invalid URI parameter data type format.")
                .data(detailMessage)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 5. NEW: Handles Wrong HTTP Methods (e.g., making a POST request to a GET-only endpoint)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        StandardResponse<Void> response = StandardResponse.<Void>builder()
                .success(false)
                .message(String.format("HTTP method '%s' is not supported for this endpoint routing route.", ex.getMethod()))
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 6. Generic Catch-All fallback for unexpected framework explosions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Void>> handleGenericException(Exception ex) {
        StandardResponse<Void> response = StandardResponse.<Void>builder()
                .success(false)
                .message("An unexpected runtime processing error occurred: " + ex.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}