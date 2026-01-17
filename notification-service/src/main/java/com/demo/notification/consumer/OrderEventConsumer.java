package com.demo.notification.consumer;

import com.demo.notification.config.RabbitMQConfig;
import com.demo.notification.dto.OrderEvent;
import com.demo.notification.service.NotificationService;
import com.demo.notification.service.RetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer component for receiving order events from RabbitMQ
 * Implements retry logic with exponential backoff and DLQ handling
 * 
 * Note: Using manual acknowledgment mode to control when messages are removed from queue
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    
    private final NotificationService notificationService;
    private final RetryService retryService;
    
    /**
     * Listen to main notification queue and process order created events
     * 
     * @param orderEvent the order event received from RabbitMQ (auto-deserialized from JSON)
     * @param message the raw RabbitMQ message (for accessing headers)
     */
    @RabbitListener(queues = RabbitMQConfig.MAIN_QUEUE)
    public void handleOrderCreated(OrderEvent orderEvent, Message message) {
        log.info("========================================");
        log.info("📨 Received OrderEvent from RabbitMQ (Main Queue)");
        log.info("   Order ID: {}", orderEvent.getOrderId());
        log.info("   Customer ID: {}", orderEvent.getCustomerId());
        log.info("   Amount: ${}", orderEvent.getAmount());
        log.info("   Status: {}", orderEvent.getStatus());
        log.info("   Created At: {}", orderEvent.getCreatedAt());
        
        // Check retry count from headers
        if (message.getMessageProperties() != null && message.getMessageProperties().getHeaders() != null) {
            Object retryCount = message.getMessageProperties().getHeaders().get("x-retry-count");
            if (retryCount != null) {
                log.info("   Retry Count: {}", retryCount);
            }
        }
        log.info("========================================");
        
        try {
            // Process notification
            notificationService.processOrderNotification(orderEvent);
            
            log.info("✅ Order event processed successfully: orderId={}", orderEvent.getOrderId());
            
            // Message will be auto-acknowledged on successful completion
            
        } catch (Exception e) {
            log.error("❌ Failed to process order event: orderId={}, error={}", 
                    orderEvent.getOrderId(), e.getMessage(), e);
            
            // Handle retry or send to DLQ
            // This will send message to retry queue or DLQ
            retryService.handleRetry(orderEvent, e, message);
            
            // Message will be auto-acknowledged (removed from main queue)
            // because it's been sent to retry/DLQ
        }
    }
    
    /**
     * Listen to Dead Letter Queue for manual processing
     * 
     * @param orderEvent the order event that failed after max retries
     * @param message the raw RabbitMQ message with failure details
     */
    @RabbitListener(queues = RabbitMQConfig.DLQ)
    public void handleDeadLetter(OrderEvent orderEvent, Message message) {
        log.error("========================================");
        log.error("💀 Received message from Dead Letter Queue");
        log.error("   Order ID: {}", orderEvent.getOrderId());
        log.error("   Customer ID: {}", orderEvent.getCustomerId());
        log.error("   Amount: ${}", orderEvent.getAmount());
        
        // Extract failure information from headers
        if (message.getMessageProperties() != null && message.getMessageProperties().getHeaders() != null) {
            Object retryCount = message.getMessageProperties().getHeaders().get("x-retry-count");
            Object failureReason = message.getMessageProperties().getHeaders().get("x-failure-reason");
            Object failureTimestamp = message.getMessageProperties().getHeaders().get("x-failure-timestamp");
            
            log.error("   Retry Count: {}", retryCount);
            log.error("   Failure Reason: {}", failureReason);
            log.error("   Failure Timestamp: {}", failureTimestamp);
        }
        
        log.error("========================================");
        log.error("⚠️  Manual intervention required for orderId={}", orderEvent.getOrderId());
        
        // In production, you might want to:
        // 1. Send alert to monitoring system (PagerDuty, Slack, etc.)
        // 2. Store in database for manual review
        // 3. Notify admin team
        // 4. Implement manual retry endpoint
    }
}
