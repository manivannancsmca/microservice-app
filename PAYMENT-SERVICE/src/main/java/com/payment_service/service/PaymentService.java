package com.payment_service.service;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment_service.client.InventoryClient;
import com.payment_service.dto.InventoryResponse;
import com.payment_service.dto.PaymentRequest;
import com.payment_service.dto.PaymentResponse;
import com.payment_service.dto.StandardResponse;
import com.payment_service.entity.Payment;
import com.payment_service.enums.PaymentStatus;
import com.payment_service.exception.InsufficientStockException;
import com.payment_service.exception.InvalidPaymentAmountException;
import com.payment_service.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InventoryClient inventoryClient;

    
    public PaymentResponse processPayment(PaymentRequest request) {
        
        // Step 1: Execute final Feign verification check to Inventory Service
        ResponseEntity<StandardResponse<InventoryResponse>> response = inventoryClient.checkStock(request.getProductId(), request.getQuantity());
        
        InventoryResponse inventoryResponse = response.getBody().getData();


        if (response == null || !inventoryResponse.isAvailable()) {
            log.warn("Payment rejected due to insufficient current state allocations for item: {}", request.getProductId());
            throw new InsufficientStockException("Requested quantity no longer available inside validation state.");
        }

        // Step 2: Establish base payment record
        Payment payment = Payment.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .orderId(request.getOrderId())
                .quantity(request.getQuantity())
                .amount(request.getAmount())
                .paymentStatus(request.getStatus()) // Assuming external provider settlement resolves cleanly here
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment entity successfully committed to localized persistence instance. ID: {}", savedPayment.getId());

        return PaymentResponse.builder()
                .paymentId(savedPayment.getId())
                .orderId(savedPayment.getOrderId())
                .status(savedPayment.getPaymentStatus().name())
                .build();
    }

    public PaymentResponse updatePayment(PaymentRequest request) {

        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for orderId: " + request.getOrderId()));

        // Validate amount
        if (payment.getAmount().compareTo(request.getAmount()) != 0) {
            throw new InvalidPaymentAmountException(
                    String.format(
                            "Invalid payment amount. Expected: %s, Received: %s",
                            payment.getAmount(),
                            request.getAmount()));
        }
        payment.setPaymentStatus(request.getStatus());

        Payment updatedPayment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(updatedPayment.getId())
                .userId(updatedPayment.getUserId())
                .productId(updatedPayment.getProductId())
                .orderId(updatedPayment.getOrderId())
                .quantity(updatedPayment.getQuantity())
                .amount(updatedPayment.getAmount())
                .status(updatedPayment.getPaymentStatus().name())
                .build();
    }

}
