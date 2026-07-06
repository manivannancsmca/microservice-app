package com.inventory_service.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory_service.dto.InventoryRequest;
import com.inventory_service.dto.InventoryResponse;
import com.inventory_service.entity.Inventory;
import com.inventory_service.exception.InsufficientStockException;
import com.inventory_service.repository.InventoryRepository;
import com.inventory_service.repository.ProcessedMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProcessedMessageRepository messageRepository;
    private static final String CONSUMER_GROUP = "inventory-saga-group";

    @Transactional
    public InventoryResponse updateOrInitializeStock(InventoryRequest request) {

        Inventory inventory = inventoryRepository
                .findByProductId(request.productId())
                .orElseGet(() -> {
                    Inventory inv = new Inventory();
                    inv.setProductId(request.productId());
                    inv.setReservedQuantity(0);
                    return inv;
                });

        // Set the new available stock level
        inventory.setAvailableQuantity(request.availableQuantity());
        Inventory savedEntity = inventoryRepository.save(inventory);

        // Return the clean response DTO
        return new InventoryResponse(
                savedEntity.getProductId(),
                savedEntity.getAvailableQuantity(),
                savedEntity.getReservedQuantity(),
                savedEntity.getAvailableQuantity() > 0);
    }

    @Transactional
    public void reserveStock(String eventId, long orderId, long userId, long productId,
            int quantity, BigDecimal price) {

        if (messageRepository.existsByMessageIdAndConsumerGroup(eventId, CONSUMER_GROUP)) {
            return;
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InsufficientStockException("Product " + productId + " not found in database."));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }

    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(Long productId) {

        return inventoryRepository.findByProductId(productId)
                .map(inventory -> new InventoryResponse(
                        inventory.getProductId(),
                        inventory.getAvailableQuantity(),
                        inventory.getReservedQuantity(),
                        inventory.getAvailableQuantity() > 0))
                .orElseThrow(() -> new InsufficientStockException(
                        "Product " + productId + " is not registered in the inventory catalog."));
    }

}
