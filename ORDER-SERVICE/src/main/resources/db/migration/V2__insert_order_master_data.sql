INSERT INTO orders (
    userId,
    productId,
    quantity,
    totalPrice,
    orderStatus,
    createdAt,
    updatedAt
) VALUES
(101, 1001, 2, 1999.98, 'PENDING',    NOW(), NOW()),
(102, 1002, 1,  899.99, 'CONFIRMED',  NOW(), NOW()),
(103, 1003, 5, 7499.95, 'PROCESSING', NOW(), NOW()),
(104, 1004, 3, 3597.00, 'SHIPPED',    NOW(), NOW()),
(105, 1005, 2, 2599.98, 'DELIVERED',  NOW(), NOW()),
(106, 1006, 4, 6399.96, 'PENDING',    NOW(), NOW()),
(107, 1007, 1,14999.00, 'CONFIRMED',  NOW(), NOW()),
(108, 1008, 6, 1794.00, 'PROCESSING', NOW(), NOW()),
(109, 1009, 2, 4998.00, 'SHIPPED',    NOW(), NOW()),
(110, 1010, 1, 1299.00, 'DELIVERED',  NOW(), NOW());