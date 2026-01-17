package com.demo.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for Order Service
 * Implements 3-queue pattern: Main Queue, Retry Queue, Dead Letter Queue
 * 
 * Flow:
 * 1. Main Queue (notification.queue) - Normal processing
 * 2. Retry Queue (notification.retry) - Retry with exponential backoff
 * 3. Dead Letter Queue (notification.dlq) - Manual processing after max retries
 */
@Configuration
public class RabbitMQConfig {
    
    // RabbitMQ constants
    public static final String EXCHANGE = "orders.exchange";
    public static final String DLX = "orders.dlx"; // Dead Letter Exchange
    public static final String MAIN_QUEUE = "notification.queue";
    public static final String RETRY_QUEUE = "notification.retry";
    public static final String DLQ = "notification.dlq";
    public static final String ROUTING_KEY = "order.created";
    public static final String RETRY_ROUTING_KEY = "notification.retry";
    public static final String DLQ_ROUTING_KEY = "notification.dlq";
    
    // TTL settings (in milliseconds)
    private static final long MAIN_QUEUE_TTL = 300000L; // 5 minutes - messages expire if not processed
    
    // ⚠️ NOTE: The following constants are for REFERENCE/DOCUMENTATION only
    // They are NOT used by Order Service (Publisher)
    // The actual retry logic is implemented in Notification Service (Consumer)
    // These values document the retry strategy used by the consumer
    private static final long RETRY_DELAY_1 = 5000L;    // 5 seconds - first retry (reference only)
    private static final long RETRY_DELAY_2 = 10000L;   // 10 seconds - second retry (reference only)
    private static final long RETRY_DELAY_3 = 20000L;   // 20 seconds - third retry (reference only)
    private static final int MAX_RETRIES = 3;            // Maximum retry attempts (reference only)
    
    /**
     * Main Exchange for order events
     */
    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(EXCHANGE);
    }
    
    /**
     * Dead Letter Exchange (DLX)
     * Messages that cannot be processed are routed here
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX);
    }
    
    /**
     * Main Queue - Normal processing queue
     * TTL: 5 minutes (messages expire if not processed in time)
     * DLX: Routes expired/failed messages to retry queue
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
     * Retry Queue - Temporary queue for retry with exponential backoff
     * TTL: Set per message (5s, 10s, 20s)
     * DLX: Routes messages back to main queue after TTL expires
     * Max retries: 3 times
     */
    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable(RETRY_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)
                .build();
    }
    
    /**
     * Dead Letter Queue - Final destination for failed messages
     * No TTL - messages stay here for manual processing
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ)
                .build();
    }
    
    /**
     * Bind Main Queue to Main Exchange
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
     * Messages from main queue (expired/failed) go to retry queue
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
     * Messages that exceed max retries go to DLQ
     */
    @Bean
    public Binding dlqBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DLQ_ROUTING_KEY);
    }
    
    /**
     * Message Converter: Auto convert Java Object <-> JSON
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitAdmin for managing RabbitMQ resources
     * Automatically declares queue, exchange, and binding on startup
     * Only Order Service declares these resources (Publisher owns the queue definition)
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    
    /**
     * RabbitTemplate with MessageConverter
     * Used to publish messages to RabbitMQ
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
