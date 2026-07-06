package com.inventory_service.messaging;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.common.avro.schemas.ProductCreatedEvent;
import com.inventory_service.entity.Inventory;
import com.inventory_service.entity.ProcessedMessage;
import com.inventory_service.repository.InventoryRepository;
import com.inventory_service.repository.ProcessedMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventConsumer {

    private static final String CONSUMER_GROUP = "inventory-product-group";

    private final InventoryRepository inventoryRepository;
    private final ProcessedMessageRepository messageRepository;

    @KafkaListener(topics = "product-created-events", groupId = CONSUMER_GROUP)
    @Transactional
    public void handleProductCreated(ConsumerRecord<String, ProductCreatedEvent> record) {
    
        ProductCreatedEvent event = record.value();
        String eventId = event.getEventId().toString();
        Long productId = event.getProductId();

        // 1. Enforce Idempotency check
        if (messageRepository.existsByMessageIdAndConsumerGroup(eventId, CONSUMER_GROUP)) {
            log.warn("Product created event already processed. Skipping message ID: {}", eventId);
            return;
        }

        // 2. Check if inventory initialization is already done
        if (inventoryRepository.findByProductId(productId).isPresent()) {
            log.info("Inventory record already exists for product: {}. Skipping step.", productId);
            
            ProcessedMessage message = ProcessedMessage.builder().messageId(eventId).consumerGroup(CONSUMER_GROUP).build();
            messageRepository.save(message);
            return;
        }

        Inventory defaultInventory = new Inventory();
        defaultInventory.setProductId(productId);
        defaultInventory.setAvailableQuantity(0);  // Initialized at 0, awaiting warehouse upload
        defaultInventory.setReservedQuantity(0);

        inventoryRepository.save(defaultInventory);

        // 4. Record event execution history to guarantee exactly-once processing behavior
        ProcessedMessage message = ProcessedMessage.builder().messageId(eventId).consumerGroup(CONSUMER_GROUP).build();
        messageRepository.save(message);
        log.info("Asynchronously initialized default inventory allocation to 0 for Product SKU: {}", productId);
    }
}
