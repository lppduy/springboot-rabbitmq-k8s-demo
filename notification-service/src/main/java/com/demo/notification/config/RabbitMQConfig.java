package com.demo.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for Notification Service
 * 
 * Note: This service declares the queue to ensure it exists when starting.
 * If Order Service has already declared the queue, declaration exceptions are ignored.
 */
@Configuration
public class RabbitMQConfig {
    
    // RabbitMQ constants - must match Order Service configuration
    public static final String EXCHANGE = "orders.exchange";
    public static final String QUEUE = "notification.queue";
    public static final String ROUTING_KEY = "order.created";
    
    /**
     * Declare Direct Exchange for order events
     * Must match Order Service configuration
     */
    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(EXCHANGE);
    }
    
    /**
     * Declare Queue for notifications
     * Must match Order Service configuration exactly (including x-message-ttl)
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-message-ttl", 60000) // Message TTL: 60 seconds (must match Order Service)
                .build();
    }
    
    /**
     * Bind Queue to Exchange with Routing Key
     * Must match Order Service configuration
     */
    @Bean
    public Binding binding(Queue notificationQueue, DirectExchange ordersExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(ordersExchange)
                .with(ROUTING_KEY);
    }
    
    /**
     * Message Converter: Auto deserialize JSON to Java Object
     * Required for @RabbitListener to deserialize messages
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitAdmin for managing RabbitMQ resources
     * ignoreDeclarationExceptions(true) allows this service to start even if
     * Order Service has already declared the queue with the same configuration.
     * If configurations differ, an exception will still be thrown.
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setIgnoreDeclarationExceptions(true);
        return admin;
    }
}
