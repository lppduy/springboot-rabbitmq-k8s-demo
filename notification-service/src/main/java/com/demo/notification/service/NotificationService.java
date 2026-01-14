package com.demo.notification.service;

import com.demo.notification.dto.OrderEvent;

/**
 * Service interface for notification operations
 * Defines contract for processing order notifications
 */
public interface NotificationService {
    
    /**
     * Process order event and send notifications
     * 
     * @param orderEvent the order event received from RabbitMQ
     */
    void processOrderNotification(OrderEvent orderEvent);
}
