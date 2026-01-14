package com.demo.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new order
 * All validation rules are defined here using Bean Validation annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new order")
public class CreateOrderRequest {
    
    @NotBlank(message = "Customer ID is required")
    @Schema(description = "Customer identifier", example = "CUST-001", required = true)
    private String customerId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "1000000", message = "Amount exceeds maximum limit of 1,000,000")
    @Schema(description = "Order amount", example = "99.99", required = true)
    private BigDecimal amount;
}
