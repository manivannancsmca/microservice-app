package com.product_write_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.common.avro.schemas.AvroProductSyncEvent;
import com.product_write_service.event.ProductInternalEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductEventListener {

    private final KafkaTemplate<String, AvroProductSyncEvent> kafkaTemplate;
    private static final String TOPIC = "product-sync-events";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductCommit(ProductInternalEvent event) {
        log.info("MySQL transaction committed successfully. Forwarding Avro payload to Kafka topic '{}'", TOPIC);

        // Directly pipe the compiled Avro model straight into your Kafka Outbox
        kafkaTemplate.send(TOPIC, event.getProductId(), event.getAvroProductSyncEvent());
    }
}
