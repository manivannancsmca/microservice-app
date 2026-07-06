INSERT INTO processedMessages
(messageId, consumerGroup, processedAt)
VALUES
('MSG-100001', 'inventory-service', NOW(6)),
('MSG-100002', 'inventory-service', NOW(6)),
('MSG-100003', 'inventory-service', NOW(6)),
('MSG-100004', 'payment-service', NOW(6)),
('MSG-100005', 'payment-service', NOW(6)),
('MSG-100006', 'notification-service', NOW(6)),
('MSG-100007', 'notification-service', NOW(6)),
('MSG-100008', 'order-service', NOW(6)),
('MSG-100009', 'order-service', NOW(6)),
('MSG-100010', 'shipping-service', NOW(6));