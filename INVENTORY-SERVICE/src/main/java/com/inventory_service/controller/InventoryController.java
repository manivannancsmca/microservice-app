package com.inventory_service.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory_service.dto.InventoryRequest;
import com.inventory_service.dto.InventoryResponse;
import com.inventory_service.dto.StandardResponse;
import com.inventory_service.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<StandardResponse<InventoryResponse>> getStock(@PathVariable Long productId) {
        InventoryResponse data = inventoryService.getInventoryByProductId(productId);
        StandardResponse<InventoryResponse> response = StandardResponse.<InventoryResponse>builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Inventory data retrieved successfully")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stock")
    public ResponseEntity<StandardResponse<InventoryResponse>> updateStock(
            @Valid @RequestBody InventoryRequest request) {

        InventoryResponse data = inventoryService.updateOrInitializeStock(request);
        StandardResponse<InventoryResponse> response = StandardResponse.<InventoryResponse>builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Inventory data created successfully")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }
}
