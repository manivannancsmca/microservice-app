package com.product_write_service.service;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.avro.schemas.AvroProductSyncEvent;
import com.product_write_service.dto.ProductRequest;
import com.product_write_service.dto.ProductResponse;
import com.product_write_service.entity.ProductCommandEntity;
import com.product_write_service.event.ProductInternalEvent;
import com.product_write_service.repository.ProductCommandRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductCommandRepository productCommandRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public String createProduct(ProductRequest request) {

        String generatedId = UUID.randomUUID().toString();
        ProductCommandEntity product = ProductCommandEntity.builder()
                .id(generatedId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .skuCode(request.getSkuCode())
                .build();

        productCommandRepository.save(product);

        AvroProductSyncEvent avroPayload = AvroProductSyncEvent.newBuilder()
                .setProductId(generatedId)
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setPrice(product.getPrice().toString())
                .setSkucode(product.getSkuCode())
                .build();

        log.info("Publishing internal application event wrapping Avro schema for ID: {}", generatedId);

        // 3. Publish within Spring Context
        eventPublisher.publishEvent(new ProductInternalEvent(this, generatedId, "CREATE", avroPayload));

        return generatedId;
    }

    @Transactional
    public void updateProduct(String id, ProductRequest request) {
        ProductCommandEntity product = productCommandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSkuCode(request.getSkuCode());

        productCommandRepository.save(product);
        log.info("Updated product in MySQL database with ID: {}", id);

        AvroProductSyncEvent avroPayload = AvroProductSyncEvent.newBuilder()
                .setProductId(id)
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setPrice(product.getPrice().toString())
                .setSkucode(product.getSkuCode())
                .build();

        eventPublisher.publishEvent(new ProductInternalEvent(this, id, "UPDATE", avroPayload));
    }

    @Transactional
    public void deleteProduct(String id) {
        if (!productCommandRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with ID: " + id);
        }

        productCommandRepository.deleteById(id);
        log.info("Deleted product from MySQL database with ID: {}", id);

        // For deletion, passing null as entity since it no longer exists
        eventPublisher.publishEvent(new ProductInternalEvent(this, id, "DELETE", null));
    }

}
