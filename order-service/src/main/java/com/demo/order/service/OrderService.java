package com.demo.order.service;

import com.demo.order.dto.CreateOrderRequest;
import com.demo.order.dto.OrderEvent;
import com.demo.order.dto.OrderResponse;
import com.demo.order.entity.Order;
import com.demo.order.publisher.OrderEventPublisher;
import com.demo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service layer for order business logic
 * Handles order creation, database persistence, and event publishing
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    
    /**
     * Create a new order, save to database, and publish event to RabbitMQ
     * 
     * @param request the order creation request
     * @return OrderResponse with order details
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Validate request
        validateOrderRequest(request);
        
        // Generate unique order ID
        String orderId = generateOrderId();
        log.info("Creating order: orderId={}, customerId={}, amount={}", 
                orderId, request.getCustomerId(), request.getAmount());
        
        // Create and save order entity to database
        Order order = Order.builder()
                .orderId(orderId)
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();
        
        order = orderRepository.save(order);
        log.info("Order saved to database: orderId={}", orderId);
        
        // Build order event for RabbitMQ
        OrderEvent orderEvent = new OrderEvent(
            order.getOrderId(),
            order.getCustomerId(),
            order.getAmount(),
            order.getStatus(),
            order.getCreatedAt()
        );
        
        // Publish event to RabbitMQ
        orderEventPublisher.publishOrderCreated(orderEvent);
        
        // Build response
        OrderResponse response = new OrderResponse(
            order.getOrderId(),
            order.getCustomerId(),
            order.getAmount(),
            order.getStatus(),
            order.getCreatedAt()
        );
        
        log.info("Order created successfully: orderId={}", orderId);
        return response;
    }
    
    /**
     * Get order by ID
     * 
     * @param orderId the order identifier
     * @return OrderResponse with order details
     * @throws IllegalArgumentException if order not found
     */
    public OrderResponse getOrderById(String orderId) {
        log.info("Fetching order: orderId={}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        
        return new OrderResponse(
            order.getOrderId(),
            order.getCustomerId(),
            order.getAmount(),
            order.getStatus(),
            order.getCreatedAt()
        );
    }
    
    /**
     * Get all orders by customer ID
     * 
     * @param customerId the customer identifier
     * @return List of OrderResponse
     */
    public java.util.List<OrderResponse> getOrdersByCustomerId(String customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        
        java.util.List<Order> orders = orderRepository.findByCustomerId(customerId);
        
        return orders.stream()
                .map(order -> new OrderResponse(
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getAmount(),
                    order.getStatus(),
                    order.getCreatedAt()
                ))
                .toList();
    }
    
    /**
     * Validate order request
     */
    private void validateOrderRequest(CreateOrderRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than zero");
        }
        
        if (request.getAmount().compareTo(new BigDecimal("1000000")) > 0) {
            throw new IllegalArgumentException("Order amount exceeds maximum limit of 1,000,000");
        }
    }
    
    /**
     * Generate unique order ID
     */
    private String generateOrderId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
