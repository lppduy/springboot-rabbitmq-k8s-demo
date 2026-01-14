package com.demo.order.dto;

import com.demo.order.shared.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Wrapper class for Swagger documentation
 * Represents BaseResponse<OrderResponse> structure
 */
@Schema(description = "Order API response wrapper")
public class OrderResponseWrapper extends BaseResponse<OrderResponse> {
    
    public OrderResponseWrapper() {
        super();
    }
    
    public OrderResponseWrapper(int status, String message, OrderResponse data) {
        super(status, message, data);
    }
}
