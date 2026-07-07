package com.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

    private Long paymentId;
    
    private Long userId;

    private Long productId;

    private Long orderId;

    private Integer quantity;

    private BigDecimal amount;

    private String status;
}
