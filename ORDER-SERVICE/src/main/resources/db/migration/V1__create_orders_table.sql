-- Location: src/main/resources/db/migration/V1__create_orders_table.sql

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT NOT NULL,
    userId BIGINT(255) NOT NULL,
    productId BIGINT(255) NOT NULL,
    quantity INT NOT NULL,
    totalPrice DECIMAL(19,2) NOT NULL,
    orderStatus VARCHAR(50) NOT NULL,
    createdAt DATETIME(6) NOT NULL,
    updatedAt DATETIME(6) NOT NULL,

    PRIMARY KEY (id)
);