package com.demo.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for Notification Service
 * Defines queue, exchange, binding, and message converter for consuming messages
 */
@Configuration
public class RabbitMQConfig {
    
    // RabbitMQ constants - must match Order Service configuration
    public static final String EXCHANGE = "orders.exchange";
    public static final String QUEUE = "notification.queue";
    public static final String ROUTING_KEY = "order.created";
    
    /**
     * Declare Direct Exchange
     * Creates exchange if it doesn't exist
     */
    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(EXCHANGE);
    }
    
    /**
     * Declare Queue for consuming notifications
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }
    
    /**
     * Bind Queue to Exchange with Routing Key
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
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
