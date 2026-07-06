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
(1001, 120, 10, NOW(6), NOW(6)),
(1002, 80, 5, NOW(6), NOW(6)),
(1003, 250, 20, NOW(6), NOW(6)),
(1004, 40, 8, NOW(6), NOW(6)),
(1005, 150, 15, NOW(6), NOW(6)),
(1006, 300, 30, NOW(6), NOW(6)),
(1007, 90, 12, NOW(6), NOW(6)),
(1008, 60, 6, NOW(6), NOW(6)),
(1009, 500, 50, NOW(6), NOW(6)),
(1010, 175, 25, NOW(6), NOW(6));