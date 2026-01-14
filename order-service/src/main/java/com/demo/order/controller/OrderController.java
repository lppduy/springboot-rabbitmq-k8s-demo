package com.demo.order.controller;

import com.demo.order.dto.CreateOrderRequest;
import com.demo.order.dto.OrderResponse;
import com.demo.order.service.OrderService;
import com.demo.order.shared.response.BaseResponse;
import com.demo.order.shared.response.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for order management
 * Provides endpoints for creating orders and publishing events
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * Create a new order
     * Creates an order and publishes event to RabbitMQ for notification
     * 
     * @param request order creation request with customer ID and amount
     * @return Created order details with status 201
     */
    @PostMapping
    @Operation(
        summary = "Create new order",
        description = "Creates a new order and publishes an event to RabbitMQ for downstream processing (notifications, inventory, etc.)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        ),
        @ApiResponse(
            responseCode = "503",
            description = "RabbitMQ service unavailable",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        )
    })
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        
        log.info("Received create order request: customerId={}, amount={}", 
                request.getCustomerId(), request.getAmount());
        
        OrderResponse response = orderService.createOrder(request);
        
        return ResponseUtils.created(
            "Order created successfully and notification event published", 
            response
        );
    }
    
    /**
     * Get order by ID
     * 
     * @param orderId the order identifier
     * @return Order details
     */
    @GetMapping("/{orderId}")
    @Operation(
        summary = "Get order by ID",
        description = "Retrieve order details by order ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order found",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content(schema = @Schema(implementation = BaseResponse.class))
        )
    })
    public ResponseEntity<BaseResponse<OrderResponse>> getOrderById(@PathVariable String orderId) {
        log.info("Fetching order: orderId={}", orderId);
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseUtils.ok("Order retrieved successfully", response);
    }
    
    /**
     * Get all orders by customer ID
     * 
     * @param customerId the customer identifier
     * @return List of orders for the customer
     */
    @GetMapping
    @Operation(
        summary = "Get orders by customer",
        description = "Retrieve all orders for a specific customer"
    )
    public ResponseEntity<BaseResponse<java.util.List<OrderResponse>>> getOrdersByCustomer(
            @RequestParam String customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        java.util.List<OrderResponse> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseUtils.ok(
            String.format("Found %d orders for customer %s", orders.size(), customerId), 
            orders
        );
    }
}
