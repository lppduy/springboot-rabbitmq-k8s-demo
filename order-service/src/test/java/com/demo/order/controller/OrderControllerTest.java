package com.demo.order.controller;

import com.demo.order.dto.CreateOrderRequest;
import com.demo.order.dto.OrderResponse;
import com.demo.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for OrderController
 */
@WebMvcTest(OrderController.class)
@DisplayName("OrderController Tests")
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private OrderService orderService;
    
    @Test
    @DisplayName("Should create order successfully")
    void createOrder_Success() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest("CUST-001", new BigDecimal("99.99"));
        OrderResponse response = new OrderResponse(
            UUID.randomUUID().toString(),
            "CUST-001",
            new BigDecimal("99.99"),
            "CREATED",
            LocalDateTime.now()
        );
        
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.orderId").exists())
                .andExpect(jsonPath("$.data.customerId").value("CUST-001"))
                .andExpect(jsonPath("$.data.amount").value(99.99));
    }
    
    @Test
    @DisplayName("Should return 400 when validation fails")
    void createOrder_ValidationFailed() throws Exception {
        // Given - Invalid request (negative amount)
        CreateOrderRequest request = new CreateOrderRequest("CUST-001", new BigDecimal("-10"));
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should get order by ID successfully")
    void getOrderById_Success() throws Exception {
        // Given
        String orderId = UUID.randomUUID().toString();
        OrderResponse response = new OrderResponse(
            orderId,
            "CUST-001",
            new BigDecimal("99.99"),
            "CREATED",
            LocalDateTime.now()
        );
        
        when(orderService.getOrderById(orderId)).thenReturn(response);
        
        // When & Then
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.customerId").value("CUST-001"));
    }
    
    @Test
    @DisplayName("Should get orders by customer ID successfully")
    void getOrdersByCustomer_Success() throws Exception {
        // Given
        String customerId = "CUST-001";
        OrderResponse response = new OrderResponse(
            UUID.randomUUID().toString(),
            customerId,
            new BigDecimal("99.99"),
            "CREATED",
            LocalDateTime.now()
        );
        
        when(orderService.getOrdersByCustomerId(customerId))
                .thenReturn(java.util.List.of(response));
        
        // When & Then
        mockMvc.perform(get("/api/orders")
                .param("customerId", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].customerId").value(customerId));
    }
}
