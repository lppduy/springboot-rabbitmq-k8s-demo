package com.demo.order.publisher;

import com.demo.order.config.RabbitMQConfig;
import com.demo.order.dto.OrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for OrderEventPublisher
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderEventPublisher Tests")
class OrderEventPublisherTest {
    
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    @InjectMocks
    private OrderEventPublisher orderEventPublisher;
    
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
    @DisplayName("Should publish order created event to RabbitMQ")
    void publishOrderCreated_Success() {
        // When
        orderEventPublisher.publishOrderCreated(orderEvent);
        
        // Then
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConfig.EXCHANGE),
            eq(RabbitMQConfig.ROUTING_KEY),
            eventCaptor.capture()
        );
        
        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getOrderId()).isEqualTo("ORD-123");
        assertThat(capturedEvent.getCustomerId()).isEqualTo("CUST-001");
        assertThat(capturedEvent.getAmount()).isEqualByComparingTo(new BigDecimal("99.99"));
    }
}
