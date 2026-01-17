package com.demo.notification.service;

import com.demo.notification.config.RabbitMQConfig;
import com.demo.notification.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementation of RetryService
 * Handles message retry logic with exponential backoff
 * Routes messages to retry queue or DLQ based on retry count
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetryServiceImpl implements RetryService {
    
    private final RabbitTemplate rabbitTemplate;
    
    // Retry delays in milliseconds (exponential backoff)
    private static final long[] RETRY_DELAYS = {5000L, 10000L, 20000L}; // 5s, 10s, 20s
    private static final int MAX_RETRIES = 3;
    private static final String RETRY_COUNT_HEADER = "x-retry-count";
    
    /**
     * Handle message retry or send to DLQ
     * 
     * @param orderEvent the order event that failed
     * @param exception the exception that occurred
     * @param message the original RabbitMQ message
     */
    @Override
    public void handleRetry(OrderEvent orderEvent, Exception exception, Message message) {
        int retryCount = getRetryCount(message);
        
        if (retryCount >= MAX_RETRIES) {
            log.error("Max retries ({}) exceeded for orderId={}. Sending to DLQ. Error: {}", 
                    MAX_RETRIES, orderEvent.getOrderId(), exception.getMessage());
            sendToDLQ(orderEvent, exception, message);
        } else {
            log.warn("Retry attempt {}/{} for orderId={}. Will retry in {}ms. Error: {}", 
                    retryCount + 1, MAX_RETRIES, orderEvent.getOrderId(), 
                    RETRY_DELAYS[retryCount], exception.getMessage());
            sendToRetryQueue(orderEvent, retryCount, message);
        }
    }
    
    /**
     * Get current retry count from message headers
     */
    private int getRetryCount(Message message) {
        if (message == null || message.getMessageProperties() == null) {
            return 0;
        }
        
        Object retryCountObj = message.getMessageProperties().getHeaders().get(RETRY_COUNT_HEADER);
        if (retryCountObj == null) {
            return 0;
        }
        
        if (retryCountObj instanceof Integer) {
            return (Integer) retryCountObj;
        }
        
        if (retryCountObj instanceof String) {
            try {
                return Integer.parseInt((String) retryCountObj);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        return 0;
    }
    
    /**
     * Send message to retry queue with TTL (exponential backoff)
     */
    private void sendToRetryQueue(OrderEvent orderEvent, int retryCount, Message originalMessage) {
        long delay = RETRY_DELAYS[retryCount];
        
        MessageProperties properties = new MessageProperties();
        properties.setExpiration(String.valueOf(delay));
        properties.setHeader(RETRY_COUNT_HEADER, retryCount + 1);
        properties.setContentType("application/json");
        
        // Copy original headers
        if (originalMessage != null && originalMessage.getMessageProperties() != null) {
            originalMessage.getMessageProperties().getHeaders().forEach((key, value) -> {
                if (!key.equals(RETRY_COUNT_HEADER) && !key.equals("x-death")) {
                    properties.setHeader(key, value);
                }
            });
        }
        
        // Convert OrderEvent to message
        Message retryMessage = rabbitTemplate.getMessageConverter().toMessage(orderEvent, properties);
        
        rabbitTemplate.send(
                RabbitMQConfig.DLX,
                RabbitMQConfig.RETRY_ROUTING_KEY,
                retryMessage
        );
        
        log.info("Message sent to retry queue. Retry count: {}, Delay: {}ms", retryCount + 1, delay);
    }
    
    /**
     * Send message to Dead Letter Queue (DLQ)
     */
    private void sendToDLQ(OrderEvent orderEvent, Exception exception, Message originalMessage) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader(RETRY_COUNT_HEADER, MAX_RETRIES);
        properties.setHeader("x-failure-reason", exception.getMessage());
        properties.setHeader("x-failure-timestamp", System.currentTimeMillis());
        properties.setContentType("application/json");
        
        // Copy original headers
        if (originalMessage != null && originalMessage.getMessageProperties() != null) {
            originalMessage.getMessageProperties().getHeaders().forEach((key, value) -> {
                if (!key.equals(RETRY_COUNT_HEADER) && !key.equals("x-death")) {
                    properties.setHeader(key, value);
                }
            });
        }
        
        // Convert OrderEvent to message
        Message dlqMessage = rabbitTemplate.getMessageConverter().toMessage(orderEvent, properties);
        
        rabbitTemplate.send(
                RabbitMQConfig.DLX,
                RabbitMQConfig.DLQ_ROUTING_KEY,
                dlqMessage
        );
        
        log.error("Message sent to DLQ. OrderId: {}, Reason: {}", orderEvent.getOrderId(), exception.getMessage());
    }
}
