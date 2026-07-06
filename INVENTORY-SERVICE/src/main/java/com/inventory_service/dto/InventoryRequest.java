package com.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryRequest(

    @NotNull(message = "Product ID is required")
    Long productId,

    @Min(value = 0, message = "Available quantity must be greater than or equal to 0")
    int availableQuantity

) {}
