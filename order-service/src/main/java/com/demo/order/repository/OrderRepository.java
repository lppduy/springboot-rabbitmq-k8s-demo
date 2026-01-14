package com.demo.order.repository;

import com.demo.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Order entity
 * Provides database operations for orders
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    /**
     * Find all orders by customer ID
     * 
     * @param customerId the customer identifier
     * @return list of orders for the customer
     */
    List<Order> findByCustomerId(String customerId);
    
    /**
     * Find all orders by status
     * 
     * @param status the order status
     * @return list of orders with the given status
     */
    List<Order> findByStatus(String status);
}
