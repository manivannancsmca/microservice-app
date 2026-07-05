package com.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.order_service.enums.OrderStatus;

@Entity
@Table(name = "orders")
@Getter                 // Safe: Only generates getters
@Setter                 // Safe: Only generates setters
@NoArgsConstructor      // Required by JPA
@AllArgsConstructor     // Useful for builders/testing
@Builder
public class Order extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    private Long userId;
    private Long productId;
    private int quantity;
    private BigDecimal totalPrice;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order user = (Order) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
}
