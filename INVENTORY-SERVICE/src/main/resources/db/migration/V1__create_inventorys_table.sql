-- Location: src/main/resources/db/migration/V1__create_inventorys_table.sql

CREATE TABLE inventory (
    id BIGINT NOT NULL AUTO_INCREMENT,
    productId BIGINT NOT NULL,
    availableQuantity INT NOT NULL DEFAULT 0,
    reservedQuantity INT NOT NULL DEFAULT 0,
    createdAt DATETIME(6) NOT NULL,
    updatedAt DATETIME(6) NOT NULL,

    CONSTRAINT pk_inventory PRIMARY KEY (id),
    CONSTRAINT ik_inventory_productId UNIQUE (productId)
);