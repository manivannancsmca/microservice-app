package com.order_service.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.order_service.dto.OrderRequest;
import com.order_service.dto.OrderResponse;
import com.order_service.dto.StandardResponse;
import com.order_service.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<StandardResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {

        OrderResponse data = orderService.createOrder(request);
        StandardResponse<OrderResponse> response = StandardResponse.<OrderResponse>builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.CREATED.value())
                .success(true)
                .message("Order created successfully")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse data = orderService.getOrderById(id);
        StandardResponse<OrderResponse> response = StandardResponse.<OrderResponse>builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Order data retrieved successfully")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByUserId(
            @PathVariable Long userId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(orderService.getOrdersByUserId(userId, pageable));
    }
}
