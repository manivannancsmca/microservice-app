package com.order_service.messaging;

import java.time.Instant;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common.avro.schemas.PaymentCompletedEvent;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletedConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "payment-completed-events", groupId = "order-saga-group")
    public void successPayment(PaymentCompletedEvent event) {

        log.info("Received success PaymentCompletedEvent: {}", event);
        paymentUpdateProcessInOrder(event);

    }

    @KafkaListener(topics = "payment-failed-events", groupId = "order-saga-group")
    public void failedPayment(PaymentCompletedEvent event) {

        log.info("Received failed PaymentCompletedEvent: {}", event);
        paymentUpdateProcessInOrder(event);

    }

    private void paymentUpdateProcessInOrder(PaymentCompletedEvent event) {
        
        Long orderId = event.getOrderId();
        String paymentStatus = event.getPaymentStatus();
        orderService.updateOrderStatus(orderId, paymentStatus);
    }

}
