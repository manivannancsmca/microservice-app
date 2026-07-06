package com.product_service.messaging;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.common.avro.schemas.ProductCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "product-created-events";
    
    public void sendProductCreatedEvent(Long productId, BigDecimal price) {
        byte[] unscaledPrice = price.unscaledValue().toByteArray();

        ProductCreatedEvent event = ProductCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setProductId(productId)
                .setPrice(ByteBuffer.wrap(unscaledPrice))
                .build();

        kafkaTemplate.send(TOPIC, String.valueOf(productId), event);
    }
}
