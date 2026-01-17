package com.demo.notification.consumer;

import com.demo.notification.dto.OrderEvent;
import com.demo.notification.service.NotificationService;
import com.demo.notification.service.RetryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

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
    
    @Mock
    private RetryService retryService;
    
    @InjectMocks
    private OrderEventConsumer orderEventConsumer;
    
    private OrderEvent orderEvent;
    private Message message;
    
    @BeforeEach
    void setUp() {
        orderEvent = new OrderEvent(
            "ORD-123",
            "CUST-001",
            new BigDecimal("99.99"),
            "CREATED",
            LocalDateTime.now()
        );
        
        // Create mock message
        MessageProperties messageProperties = new MessageProperties();
        message = new Message("test".getBytes(), messageProperties);
    }
    
    @Test
    @DisplayName("Should handle order created event successfully")
    void handleOrderCreated_Success() {
        // Given
        doNothing().when(notificationService).processOrderNotification(any(OrderEvent.class));
        
        // When
        orderEventConsumer.handleOrderCreated(orderEvent, message);
        
        // Then
        verify(notificationService, times(1)).processOrderNotification(orderEvent);
        verify(retryService, never()).handleRetry(any(), any(), any());
    }
    
    @Test
    @DisplayName("Should call retry service when notification processing fails")
    void handleOrderCreated_ProcessingFailure() {
        // Given
        RuntimeException exception = new RuntimeException("Notification failed");
        doThrow(exception)
                .when(notificationService).processOrderNotification(any(OrderEvent.class));
        
        // When
        orderEventConsumer.handleOrderCreated(orderEvent, message);
        
        // Then
        verify(notificationService, times(1)).processOrderNotification(orderEvent);
        verify(retryService, times(1)).handleRetry(orderEvent, exception, message);
    }
}
