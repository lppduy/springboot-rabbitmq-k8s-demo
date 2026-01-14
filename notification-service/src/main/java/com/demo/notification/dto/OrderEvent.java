package com.demo.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event payload for order-related messages
 * Consumed from RabbitMQ notification queue
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
