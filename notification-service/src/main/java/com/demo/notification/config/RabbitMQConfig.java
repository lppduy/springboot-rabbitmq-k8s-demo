package com.demo.notification.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for Notification Service
 * 
 * Note: Queue, Exchange, and Binding are declared by Order Service (Publisher).
 * This service only consumes messages from the queue.
 */
@Configuration
public class RabbitMQConfig {
    
    // RabbitMQ constants - must match Order Service configuration
    public static final String EXCHANGE = "orders.exchange";
    public static final String QUEUE = "notification.queue";
    public static final String ROUTING_KEY = "order.created";
    
    /**
     * Message Converter: Auto deserialize JSON to Java Object
     * Required for @RabbitListener to deserialize messages
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
