package com.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long productId;
    private Long userId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String orderStatus;
    private Instant  createdAt;
}
