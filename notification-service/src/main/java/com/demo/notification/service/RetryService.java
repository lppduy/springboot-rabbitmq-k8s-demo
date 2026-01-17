package com.demo.notification.service;

import com.demo.notification.dto.OrderEvent;
import org.springframework.amqp.core.Message;

/**
 * Service interface for handling message retry logic with exponential backoff
 * Routes messages to retry queue or DLQ based on retry count
 */
public interface RetryService {
    
    /**
     * Handle message retry or send to DLQ
     * 
     * @param orderEvent the order event that failed
     * @param exception the exception that occurred
     * @param message the original RabbitMQ message
     */
    void handleRetry(OrderEvent orderEvent, Exception exception, Message message);
}
