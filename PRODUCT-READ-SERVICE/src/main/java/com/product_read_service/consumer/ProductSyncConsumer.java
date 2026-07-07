package com.product_read_service.consumer;

import java.math.BigDecimal;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common.avro.schemas.AvroProductSyncEvent;
import com.product_read_service.document.ProductDocument;
import com.product_read_service.repository.ProductSearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductSyncConsumer {

    private final ProductSearchRepository searchRepository;

    @KafkaListener(topics = "product-sync-events", groupId = "product-query-sync-group")
    public void consumeSyncEvent(ConsumerRecord<String, AvroProductSyncEvent> record) {

        AvroProductSyncEvent avroProductEvent = record.value();
        String productId = record.key();

        log.info("Received Avro Sync action [{}] for Product ID: {}", avroProductEvent.getAction(), productId);
        // Handle deletions smoothly
        if ("DELETE".equalsIgnoreCase(avroProductEvent.getAction().toString())) {
            searchRepository.deleteById(productId);
            log.info("Successfully evicted Product ID: {} from Elasticsearch index.", productId);
            return;
        }

        // Map data safely out of Avro wrapper types into standard data formats
        ProductDocument document = ProductDocument.builder()
                .id(avroProductEvent.getProductId().toString())
                .name(avroProductEvent.getName().toString())
                .description(avroProductEvent.getDescription().toString())
                .price(new BigDecimal(avroProductEvent.getPrice().toString()))
                .skuCode(avroProductEvent.getSkucode())
                .build();

                // Save data to the Elasticsearch cluster database
        searchRepository.save(document);
        log.info("Successfully updated search index for Product ID: {}", productId);
    }
}
