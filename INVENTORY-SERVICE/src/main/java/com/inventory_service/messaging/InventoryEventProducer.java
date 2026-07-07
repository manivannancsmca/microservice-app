package com.inventory_service.messaging;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.common.avro.schemas.InventoryAllocatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendInventoryAllocated(long orderId, long productId, int quantity, BigDecimal totalPrice) {

        byte[] unscaledPrice = totalPrice.unscaledValue().toByteArray();

        InventoryAllocatedEvent event = InventoryAllocatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(orderId)
                .setProductId(productId)
                .setQuantity(quantity)
                .setTotalPrice(ByteBuffer.wrap(unscaledPrice))
                .setStatus("SUCCESS")
                .build();

        kafkaTemplate.send("inventory-allocated-events", String.valueOf(orderId), event);
    }

    public void sendInventoryAllocationFailed(long orderId, long productId, int quantity) {

        InventoryAllocatedEvent event = InventoryAllocatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(orderId)
                .setProductId(productId)
                .setQuantity(quantity)
                .setTotalPrice(ByteBuffer.wrap(new byte[0]))
                .setStatus("FAILED")
                .build();

        kafkaTemplate.send("inventory-allocated-events", String.valueOf(orderId), event);
    }

}
