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
 * Defines exchange, queue, binding, and message converter
 */
@Configuration
public class RabbitMQConfig {
    
    // RabbitMQ constants
    public static final String EXCHANGE = "orders.exchange";
    public static final String QUEUE = "notification.queue";
    public static final String ROUTING_KEY = "order.created";
    
    /**
     * Declare Direct Exchange for order events
     */
    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(EXCHANGE);
    }
    
    /**
     * Declare Queue for notifications
     * Durable queue persists messages after RabbitMQ restart
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-message-ttl", 60000) // Message TTL: 60 seconds
                .build();
    }
    
    /**
     * Bind Queue to Exchange with Routing Key
     * Messages with routing key "order.created" will be routed to notification.queue
     */
    @Bean
    public Binding binding(Queue notificationQueue, DirectExchange ordersExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(ordersExchange)
                .with(ROUTING_KEY);
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
