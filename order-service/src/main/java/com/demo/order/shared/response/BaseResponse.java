package com.demo.order.shared.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard response wrapper for all API responses
 * Provides consistent response format across the application
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API response wrapper")
public class BaseResponse<T> {
    
    @Schema(description = "HTTP status code", example = "200")
    private int status;
    
    @Schema(description = "Response message", example = "Success")
    private String message;
    
    @Schema(description = "Response data payload")
    private T data;
}
