-- Location: src/main/resources/db/migration/V1__create_users_table.sql

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    shippingAddress VARCHAR(255) NULL,
    createdAt DATETIME(6) NOT NULL,
    updatedAt DATETIME(6) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_user_email UNIQUE (email)
);