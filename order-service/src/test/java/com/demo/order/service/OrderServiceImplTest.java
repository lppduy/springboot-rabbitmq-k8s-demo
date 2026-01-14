package com.demo.order.service;

import com.demo.order.dto.CreateOrderRequest;
import com.demo.order.dto.OrderEvent;
import com.demo.order.dto.OrderResponse;
import com.demo.order.entity.Order;
import com.demo.order.publisher.OrderEventPublisher;
import com.demo.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceImplTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderEventPublisher orderEventPublisher;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    private CreateOrderRequest createOrderRequest;
    private Order savedOrder;
    
    @BeforeEach
    void setUp() {
        createOrderRequest = new CreateOrderRequest("CUST-001", new BigDecimal("99.99"));
        
        String orderId = UUID.randomUUID().toString();
        savedOrder = Order.builder()
                .orderId(orderId)
                .customerId("CUST-001")
                .amount(new BigDecimal("99.99"))
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    @DisplayName("Should create order successfully")
    void createOrder_Success() {
        // Given
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(orderEventPublisher).publishOrderCreated(any(OrderEvent.class));
        
        // When
        OrderResponse response = orderService.createOrder(createOrderRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(savedOrder.getOrderId());
        assertThat(response.getCustomerId()).isEqualTo("CUST-001");
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(response.getStatus()).isEqualTo("CREATED");
        
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderEventPublisher, times(1)).publishOrderCreated(any(OrderEvent.class));
    }
    
    @Test
    @DisplayName("Should get order by ID successfully")
    void getOrderById_Success() {
        // Given
        String orderId = savedOrder.getOrderId();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(savedOrder));
        
        // When
        OrderResponse response = orderService.getOrderById(orderId);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(orderId);
        assertThat(response.getCustomerId()).isEqualTo("CUST-001");
        
        verify(orderRepository, times(1)).findById(orderId);
    }
    
    @Test
    @DisplayName("Should throw exception when order not found")
    void getOrderById_NotFound() {
        // Given
        String orderId = "non-existent-id";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order not found");
        
        verify(orderRepository, times(1)).findById(orderId);
    }
    
    @Test
    @DisplayName("Should get all orders by customer ID")
    void getOrdersByCustomerId_Success() {
        // Given
        String customerId = "CUST-001";
        Order order2 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId(customerId)
                .amount(new BigDecimal("199.99"))
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();
        
        when(orderRepository.findByCustomerId(customerId))
                .thenReturn(List.of(savedOrder, order2));
        
        // When
        List<OrderResponse> responses = orderService.getOrdersByCustomerId(customerId);
        
        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getCustomerId()).isEqualTo(customerId);
        assertThat(responses.get(1).getCustomerId()).isEqualTo(customerId);
        
        verify(orderRepository, times(1)).findByCustomerId(customerId);
    }
    
    @Test
    @DisplayName("Should return empty list when customer has no orders")
    void getOrdersByCustomerId_EmptyList() {
        // Given
        String customerId = "CUST-999";
        when(orderRepository.findByCustomerId(customerId)).thenReturn(List.of());
        
        // When
        List<OrderResponse> responses = orderService.getOrdersByCustomerId(customerId);
        
        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();
        
        verify(orderRepository, times(1)).findByCustomerId(customerId);
    }
}
