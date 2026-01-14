package com.demo.notification.service;

import com.demo.notification.dto.OrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Unit tests for NotificationServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Tests")
class NotificationServiceImplTest {
    
    @InjectMocks
    private NotificationServiceImpl notificationService;
    
    private OrderEvent orderEvent;
    
    @BeforeEach
    void setUp() {
        orderEvent = new OrderEvent(
            "ORD-123",
            "CUST-001",
            new BigDecimal("99.99"),
            "CREATED",
            LocalDateTime.now()
        );
    }
    
    @Test
    @DisplayName("Should process order notification successfully")
    void processOrderNotification_Success() {
        // When & Then - Should not throw exception
        assertThatCode(() -> notificationService.processOrderNotification(orderEvent))
                .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("Should handle notification processing for different order amounts")
    void processOrderNotification_DifferentAmounts() {
        // Given - Different order amounts
        OrderEvent smallOrder = new OrderEvent(
            "ORD-001",
            "CUST-001",
            new BigDecimal("10.00"),
            "CREATED",
            LocalDateTime.now()
        );
        
        OrderEvent largeOrder = new OrderEvent(
            "ORD-002",
            "CUST-002",
            new BigDecimal("9999.99"),
            "CREATED",
            LocalDateTime.now()
        );
        
        // When & Then
        assertThatCode(() -> {
            notificationService.processOrderNotification(smallOrder);
            notificationService.processOrderNotification(largeOrder);
        }).doesNotThrowAnyException();
    }
}
