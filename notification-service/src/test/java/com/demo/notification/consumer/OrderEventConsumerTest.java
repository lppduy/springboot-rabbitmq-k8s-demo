package com.demo.notification.consumer;

import com.demo.notification.dto.OrderEvent;
import com.demo.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderEventConsumer
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderEventConsumer Tests")
class OrderEventConsumerTest {
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private OrderEventConsumer orderEventConsumer;
    
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
    @DisplayName("Should handle order created event successfully")
    void handleOrderCreated_Success() {
        // Given
        doNothing().when(notificationService).processOrderNotification(any(OrderEvent.class));
        
        // When
        orderEventConsumer.handleOrderCreated(orderEvent);
        
        // Then
        verify(notificationService, times(1)).processOrderNotification(orderEvent);
    }
    
    @Test
    @DisplayName("Should propagate exception when notification processing fails")
    void handleOrderCreated_ProcessingFailure() {
        // Given
        doThrow(new RuntimeException("Notification failed"))
                .when(notificationService).processOrderNotification(any(OrderEvent.class));
        
        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> orderEventConsumer.handleOrderCreated(orderEvent)
        );
        
        verify(notificationService, times(1)).processOrderNotification(orderEvent);
    }
}
