package com.inventory_service.dto;


public record InventoryResponse(
    Long productId,
    int availableQuantity,
    int reservedQuantity,
    boolean isAvailable
) {}
