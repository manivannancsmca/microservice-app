package com.payment_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common.avro.schemas.InventoryAllocatedEvent;
import com.payment_service.client.OrderClient;
import com.payment_service.dto.OrderResponse;
import com.payment_service.dto.PaymentRequest;
import com.payment_service.dto.StandardResponse;
import com.payment_service.enums.PaymentStatus;
import com.payment_service.service.PaymentService;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryAllocatedConsumer {

    private final PaymentService paymentService;

    private final OrderClient orderClient;

    @KafkaListener(topics = "inventory-allocated-events", groupId = "payment-saga-group")
    public void consume(ConsumerRecord<String, InventoryAllocatedEvent> record) {
        InventoryAllocatedEvent event = record.value();
        log.info("Received InventoryAllocatedEvent for Order ID: {}", event.getOrderId());


        ResponseEntity<StandardResponse<OrderResponse>> feignOrderResponse = orderClient
                .getOrderById(event.getProductId());

        OrderResponse orderResponse = feignOrderResponse.getBody().getData();
        try {
            // Map Avro record to app internal request execution
            PaymentRequest request = new PaymentRequest();
            request.setOrderId(event.getOrderId());
            request.setProductId(event.getProductId());
            request.setQuantity(event.getQuantity());
            request.setUserId(orderResponse.getUserId());
            request.setAmount(BigDecimal.ZERO); // Determine async dynamic routing logic if applicable
            request.setStatus(PaymentStatus.PENDING);
            paymentService.processPayment(request);
        } catch (Exception e) {
            log.error("Failed to synchronously settle async payment flow for order: {}", event.getOrderId(), e);
        }
    }
}