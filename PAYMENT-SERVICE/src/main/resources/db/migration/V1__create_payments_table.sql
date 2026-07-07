-- Location: src/main/resources/db/migration/V1__create_payments_table.sql

CREATE TABLE payments (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(50) NOT NULL,
    productId VARCHAR(50) NOT NULL,
    orderId VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    paymentStatus VARCHAR(20) NOT NULL,
    createdAt DATETIME(6) NOT NULL,
    updatedAt DATETIME(6) NOT NULL,
    version INT NOT NULL,

    CONSTRAINT uk_payments_order_id UNIQUE (orderId)
);