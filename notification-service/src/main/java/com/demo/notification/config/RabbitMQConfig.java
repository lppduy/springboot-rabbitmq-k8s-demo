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
 * Implements 3-queue pattern: Main Queue, Retry Queue, Dead Letter Queue
 * 
 * Note: This service declares the queues to ensure they exist when starting.
 * If Order Service has already declared them, declaration exceptions are ignored.
 */
@Configuration
public class RabbitMQConfig {
    
    // RabbitMQ constants - must match Order Service configuration
    public static final String EXCHANGE = "orders.exchange";
    public static final String DLX = "orders.dlx"; // Dead Letter Exchange
    public static final String MAIN_QUEUE = "notification.queue";
    public static final String RETRY_QUEUE = "notification.retry";
    public static final String DLQ = "notification.dlq";
    public static final String ROUTING_KEY = "order.created";
    public static final String RETRY_ROUTING_KEY = "notification.retry";
    public static final String DLQ_ROUTING_KEY = "notification.dlq";
    
    // TTL settings (in milliseconds) - must match Order Service
    private static final long MAIN_QUEUE_TTL = 300000L; // 5 minutes
    
    /**
     * Declare Main Exchange for order events
     * Must match Order Service configuration
     */
    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(EXCHANGE);
    }
    
    /**
     * Declare Dead Letter Exchange (DLX)
     * Must match Order Service configuration
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX);
    }
    
    /**
     * Declare Main Queue for notifications
     * Must match Order Service configuration exactly
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(MAIN_QUEUE)
                .withArgument("x-message-ttl", MAIN_QUEUE_TTL)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", RETRY_ROUTING_KEY)
                .build();
    }
    
    /**
     * Declare Retry Queue
     * Must match Order Service configuration
     */
    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable(RETRY_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)
                .build();
    }
    
    /**
     * Declare Dead Letter Queue
     * Must match Order Service configuration
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ)
                .build();
    }
    
    /**
     * Bind Main Queue to Main Exchange
     * Must match Order Service configuration
     */
    @Bean
    public Binding mainQueueBinding(Queue notificationQueue, DirectExchange ordersExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(ordersExchange)
                .with(ROUTING_KEY);
    }
    
    /**
     * Bind Retry Queue to Dead Letter Exchange
     * Must match Order Service configuration
     */
    @Bean
    public Binding retryQueueBinding(Queue retryQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(retryQueue)
                .to(deadLetterExchange)
                .with(RETRY_ROUTING_KEY);
    }
    
    /**
     * Bind Dead Letter Queue to Dead Letter Exchange
     * Must match Order Service configuration
     */
    @Bean
    public Binding dlqBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DLQ_ROUTING_KEY);
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
