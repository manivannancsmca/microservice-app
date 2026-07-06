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
       
        InventoryAllocatedEvent event = InventoryAllocatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(orderId)
                .setProductId(productId)
                .setQuantity(quantity)
                .setTotalPrice(totalPrice.longValue())
                .setStatus("SUCCESS")
                .build();
    }


}
