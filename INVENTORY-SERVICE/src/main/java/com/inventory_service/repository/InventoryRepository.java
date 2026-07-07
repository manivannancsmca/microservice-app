package com.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inventory_service.dto.InventoryResponse;
import com.inventory_service.entity.Inventory;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);

    @Query("""
            SELECT new com.inventory_service.dto.InventoryResponse(
                i.productId,
                i.availableQuantity,
                i.reservedQuantity,
                CASE WHEN i.availableQuantity >= :quantity THEN true ELSE false END
            )
            FROM Inventory i
            WHERE i.productId = :productId
            """)
    Optional<InventoryResponse> checkStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity);
}
