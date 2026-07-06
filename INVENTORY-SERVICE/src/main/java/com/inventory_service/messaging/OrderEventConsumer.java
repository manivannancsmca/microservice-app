package com.inventory_service.messaging;

import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common.avro.schemas.OrderPlacedEvent;
import com.inventory_service.service.InventoryService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-placed-events", groupId = "inventory-saga-group")
    public void handleOrderPlaced(ConsumerRecord<String, OrderPlacedEvent> record) {
        
        OrderPlacedEvent event = record.value();
        String orderIdKey = record.key();
        BigDecimal price = BigDecimal.valueOf(event.getTotalAmount());

        inventoryService.reserveStock(
                orderIdKey,
                event.getOrderId(),
                event.getUserId(),
                event.getProductId(),
                event.getQuantity(),
                price
        );

    }
}
