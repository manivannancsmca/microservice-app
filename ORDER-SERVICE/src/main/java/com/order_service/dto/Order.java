package com.order_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.order_service.enums.OrderStatus;

import lombok.Getter;

@Getter
public class Order {
    private final String id;
    private final String customerId;
    private final String productId;
    private final int quantity;
    private final BigDecimal totalPrice;
    private OrderStatus status;

    public Order(String customerId, String productId, int quantity, BigDecimal totalPrice) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.PENDING;
    }

    // Rehydration constructor for persistence layer conversion
    public Order(String id, String customerId, String productId, int quantity, BigDecimal totalPrice, OrderStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public void transitionToCancelled() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed transaction order.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void transitionToCancellationRequested() {
        if (this.status == OrderStatus.PENDING) {
            this.status = OrderStatus.CANCELLATION_REQUESTED;
        }
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void fail() {
        this.status = OrderStatus.FAILED;
    }

    // Getters
    // public String getId() { return id; }
    // public String getCustomerId() { return customerId; }
    // public String getProductId() { return productId; }
    // public int getQuantity() { return quantity; }
    // public BigDecimal getTotalPrice() { return totalPrice; }
    // public OrderStatus getStatus() { return status; }
}
