-- Location: src/main/resources/db/migration/V2__seed_initial_inventory.sql

INSERT INTO inventory
(
    productId,
    availableQuantity,
    reservedQuantity,
    createdAt,
    updatedAt
)
VALUES
(1, 120, 10, NOW(6), NOW(6)),
(2, 80, 5, NOW(6), NOW(6)),
(3, 250, 20, NOW(6), NOW(6)),
(4, 40, 8, NOW(6), NOW(6)),
(5, 150, 15, NOW(6), NOW(6)),
(6, 300, 30, NOW(6), NOW(6)),
(7, 90, 12, NOW(6), NOW(6)),
(8, 60, 6, NOW(6), NOW(6)),
(9, 500, 50, NOW(6), NOW(6)),
(10, 175, 25, NOW(6), NOW(6));