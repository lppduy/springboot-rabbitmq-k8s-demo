package com.demo.order.service;

import com.demo.order.dto.CreateOrderRequest;
import com.demo.order.dto.OrderResponse;

import java.util.List;

/**
 * Service interface for order business logic
 * Defines contract for order operations
 */
public interface OrderService {
    
    /**
     * Create a new order, save to database, and publish event to RabbitMQ
     * 
     * @param request the order creation request
     * @return OrderResponse with order details
     */
    OrderResponse createOrder(CreateOrderRequest request);
    
    /**
     * Get order by ID
     * 
     * @param orderId the order identifier
     * @return OrderResponse with order details
     * @throws IllegalArgumentException if order not found
     */
    OrderResponse getOrderById(String orderId);
    
    /**
     * Get all orders by customer ID
     * 
     * @param customerId the customer identifier
     * @return List of OrderResponse
     */
    List<OrderResponse> getOrdersByCustomerId(String customerId);
}
