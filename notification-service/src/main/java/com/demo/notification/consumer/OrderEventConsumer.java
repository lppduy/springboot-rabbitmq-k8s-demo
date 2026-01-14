package com.demo.notification.consumer;

import com.demo.notification.config.RabbitMQConfig;
import com.demo.notification.dto.OrderEvent;
import com.demo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer component for receiving order events from RabbitMQ
 * Listens to notification queue and processes order events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    
    private final NotificationService notificationService;
    
    /**
     * Listen to notification queue and process order created events
     * 
     * @param orderEvent the order event received from RabbitMQ (auto-deserialized from JSON)
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleOrderCreated(OrderEvent orderEvent) {
        log.info("========================================");
        log.info("📨 Received OrderEvent from RabbitMQ");
        log.info("   Order ID: {}", orderEvent.getOrderId());
        log.info("   Customer ID: {}", orderEvent.getCustomerId());
        log.info("   Amount: ${}", orderEvent.getAmount());
        log.info("   Status: {}", orderEvent.getStatus());
        log.info("   Created At: {}", orderEvent.getCreatedAt());
        log.info("========================================");
        
        try {
            // Process notification
            notificationService.processOrderNotification(orderEvent);
            
            log.info("✅ Order event processed successfully: orderId={}", orderEvent.getOrderId());
            
        } catch (Exception e) {
            log.error("❌ Failed to process order event: orderId={}, error={}", 
                    orderEvent.getOrderId(), e.getMessage(), e);
            
            // In production, you might want to:
            // 1. Send to Dead Letter Queue (DLQ)
            // 2. Implement retry logic
            // 3. Alert monitoring system
            throw e; // Re-throw to trigger retry (if configured)
        }
    }
}
