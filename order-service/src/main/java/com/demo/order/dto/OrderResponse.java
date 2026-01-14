package com.demo.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for order creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order creation response")
public class OrderResponse {
    
    @Schema(description = "Unique order identifier", example = "ORD-ABC12345")
    private String orderId;
    
    @Schema(description = "Customer identifier", example = "CUST-001")
    private String customerId;
    
    @Schema(description = "Order amount", example = "99.99")
    private BigDecimal amount;
    
    @Schema(description = "Order status", example = "CREATED")
    private String status;
    
    @Schema(description = "Order creation timestamp")
    private LocalDateTime createdAt;
}
