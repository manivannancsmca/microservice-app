package com.product_write_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCommandEntity {
    @Id
    private String id; // UUID generated at application level for consistency across DBs
    private String name;
    private String description;
    private BigDecimal price;
    private String skuCode;
}