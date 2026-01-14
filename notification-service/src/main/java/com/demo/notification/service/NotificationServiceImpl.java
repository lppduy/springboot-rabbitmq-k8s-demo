package com.demo.notification.service;

import com.demo.notification.dto.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of NotificationService
 * Handles email and SMS notification sending
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    
    /**
     * Process order event and send notifications
     * 
     * @param orderEvent the order event received from RabbitMQ
     */
    @Override
    public void processOrderNotification(OrderEvent orderEvent) {
        log.info("Processing order notification: orderId={}, customerId={}, amount={}", 
                orderEvent.getOrderId(), 
                orderEvent.getCustomerId(), 
                orderEvent.getAmount());
        
        try {
            // Send email notification
            sendEmailNotification(orderEvent);
            
            // Send SMS notification
            sendSmsNotification(orderEvent);
            
            log.info("Notifications sent successfully for order: {}", orderEvent.getOrderId());
            
        } catch (Exception e) {
            log.error("Failed to send notifications for order: {}, error: {}", 
                    orderEvent.getOrderId(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * Send email notification (simulated)
     * In production, integrate with email service like SendGrid, AWS SES, etc.
     */
    private void sendEmailNotification(OrderEvent orderEvent) {
        log.info("📧 Sending EMAIL notification to customer: {}", orderEvent.getCustomerId());
        log.info("   Subject: Order Confirmation - {}", orderEvent.getOrderId());
        log.info("   Message: Your order {} has been created successfully!", orderEvent.getOrderId());
        log.info("   Order Amount: ${}", orderEvent.getAmount());
        log.info("   Order Status: {}", orderEvent.getStatus());
        
        // Simulate email sending delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("✅ Email sent successfully to customer: {}", orderEvent.getCustomerId());
    }
    
    /**
     * Send SMS notification (simulated)
     * In production, integrate with SMS service like Twilio, AWS SNS, etc.
     */
    private void sendSmsNotification(OrderEvent orderEvent) {
        log.info("📱 Sending SMS notification to customer: {}", orderEvent.getCustomerId());
        log.info("   Message: Order {} confirmed! Amount: ${}. Thank you for your purchase!", 
                orderEvent.getOrderId(), orderEvent.getAmount());
        
        // Simulate SMS sending delay
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("✅ SMS sent successfully to customer: {}", orderEvent.getCustomerId());
    }
}
