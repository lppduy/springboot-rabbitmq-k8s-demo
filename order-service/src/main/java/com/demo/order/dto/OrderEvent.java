package com.demo.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event payload for order-related messages
 * Shared between Order Service (publisher) and Notification Service (consumer)
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
