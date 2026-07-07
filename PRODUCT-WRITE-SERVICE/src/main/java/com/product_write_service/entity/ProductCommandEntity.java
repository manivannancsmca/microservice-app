package com.product_write_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCommandEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id; // UUID generated at application level for consistency across DBs
    private String name;
    private String description;
    private BigDecimal price;
    private String skuCode;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductCommandEntity user = (ProductCommandEntity) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}