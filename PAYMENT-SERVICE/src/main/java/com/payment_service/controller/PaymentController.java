package com.payment_service.controller;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment_service.dto.InventoryResponse;
import com.payment_service.dto.PaymentRequest;
import com.payment_service.dto.PaymentResponse;
import com.payment_service.dto.StandardResponse;
import com.payment_service.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PutMapping
    public ResponseEntity<StandardResponse<PaymentResponse>> updatePayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse data = paymentService.updatePayment(request);

        StandardResponse<PaymentResponse> response = StandardResponse.<PaymentResponse>builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Inventory data created successfully")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }
}
