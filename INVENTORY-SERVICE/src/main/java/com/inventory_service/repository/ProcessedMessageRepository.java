package com.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory_service.entity.ProcessedMessage;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, Long> {
    boolean existsByMessageIdAndConsumerGroup(String messageId, String consumerGroup);
}