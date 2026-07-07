package com.product_write_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product_write_service.dto.ProductRequest;
import com.product_write_service.dto.ProductResponse;
import com.product_write_service.dto.StandardResponse;
import com.product_write_service.service.ProductCommandService;

@RestController
@RequestMapping("/api/v1/products/commands")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    @PostMapping
    public ResponseEntity<StandardResponse<String>> createProduct(@Valid @RequestBody ProductRequest request) {

        String data = productCommandService.createProduct(request);

        StandardResponse<String> response = StandardResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .success(true)
                .message("Product created successfully")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request) {

        productCommandService.updateProduct(id, request);

        StandardResponse<Void> response = StandardResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .success(true)
                .message("Product created successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteProduct(@PathVariable String id) {

        productCommandService.deleteProduct(id);

        StandardResponse<Void> response = StandardResponse.<Void>builder()
                .success(true)
                .message("Product deletion completed successfully.")
                .build();

        return ResponseEntity.ok(response);
    }
}
