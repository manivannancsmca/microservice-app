-- Location: src/main/resources/db/migration/V1__create_processed_messages_table.sql

CREATE TABLE processedMessages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    messageId VARCHAR(255) NOT NULL,
    consumerGroup VARCHAR(255) NOT NULL,
    processedAt DATETIME(6) NOT NULL,

    CONSTRAINT pk_processed_messages PRIMARY KEY (id),

    UNIQUE KEY idx_msg_group (messageId, consumerGroup)
);