package com.demo.order.publisher;

import com.demo.order.config.RabbitMQConfig;
import com.demo.order.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publisher component for sending order events to RabbitMQ
 * Publishes OrderEvent messages to the orders exchange
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    /**
     * Publish order created event to RabbitMQ
     * 
     * @param orderEvent the order event to publish
     * @throws org.springframework.amqp.AmqpException if RabbitMQ is unavailable
     */
    public void publishOrderCreated(OrderEvent orderEvent) {
        try {
            log.info("Publishing order created event: orderId={}, customerId={}, amount={}", 
                    orderEvent.getOrderId(), 
                    orderEvent.getCustomerId(), 
                    orderEvent.getAmount());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,      // Exchange name
                RabbitMQConfig.ROUTING_KEY,   // Routing key: "order.created"
                orderEvent                     // Message payload (auto-converted to JSON)
            );
            
            log.info("Order event published successfully: orderId={}", orderEvent.getOrderId());
            
        } catch (Exception e) {
            log.error("Failed to publish order event: orderId={}, error={}", 
                    orderEvent.getOrderId(), e.getMessage());
            throw e; // Re-throw to be handled by GlobalExceptionHandler
        }
    }
}
