package com.product_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product_service.dto.ProductRequest;
import com.product_service.dto.ProductResponse;
import com.product_service.dto.StandardResponse;
import com.product_service.service.ProductService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<StandardResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        
        ProductResponse data = productService.createProduct(request);
        
        StandardResponse<ProductResponse> response = StandardResponse.<ProductResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .success(true)
                .message("Product created successfully")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        
        ProductResponse data = productService.getProductById(id);
        
        StandardResponse<ProductResponse> response = StandardResponse.<ProductResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Product retrieved successfully")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<StandardResponse<List<ProductResponse>>> getAllUsers() {
        List<ProductResponse> users = productService.getAllUsers();
        StandardResponse<List<ProductResponse>> response = StandardResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("All user profiles retrieved successfully.")
                .data(users)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/home")
    public String testProduct() {
        return "test product";
    }
}