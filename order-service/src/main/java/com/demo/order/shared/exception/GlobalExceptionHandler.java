package com.demo.order.shared.exception;

import com.demo.order.shared.response.BaseResponse;
import com.demo.order.shared.response.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for consistent error responses
 * Catches exceptions thrown by controllers and returns standardized error responses
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle validation errors from @Valid annotations
     * Returns 400 Bad Request with validation error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.error("Validation error: {}", errorMessage);
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, errorMessage);
    }
    
    /**
     * Handle RabbitMQ/AMQP errors
     * Returns 503 Service Unavailable when message queue is down
     */
    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<BaseResponse<Void>> handleAmqpException(AmqpException ex) {
        log.error("RabbitMQ error: {}", ex.getMessage(), ex);
        return ResponseUtils.error(
            HttpStatus.SERVICE_UNAVAILABLE, 
            "Message queue is temporarily unavailable. Please try again later."
        );
    }
    
    /**
     * Handle illegal argument exceptions
     * Returns 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    
    /**
     * Handle all other uncaught exceptions
     * Returns 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseUtils.error(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred. Please contact support if the issue persists."
        );
    }
}
