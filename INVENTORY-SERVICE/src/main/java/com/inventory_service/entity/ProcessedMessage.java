package com.inventory_service.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "processedMessages", indexes = @Index(name = "idx_msg_group", columnList = "messageId, consumerGroup", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "messageId", nullable = false)
    private String messageId;

    @Column(name = "consumerGroup", nullable = false)
    private String consumerGroup;

    @CreatedDate
    @Column(name = "processedAt", nullable = false, updatable = false)
    private Instant processedAt;
}
