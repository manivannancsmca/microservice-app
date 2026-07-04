-- Location: src/main/resources/db/migration/V2__seed_initial_users.sql

INSERT INTO users (name, email, shippingAddress, createdAt, updatedAt)
VALUES 
('System Admin', 'admin@company.com', 'HQ Server Room Room 101', NOW(6), NOW(6)),
('Support Team', 'support@company.com', 'Remote Office A', NOW(6), NOW(6)),
('Test User', 'test.user@company.com', NULL, NOW(6), NOW(6));