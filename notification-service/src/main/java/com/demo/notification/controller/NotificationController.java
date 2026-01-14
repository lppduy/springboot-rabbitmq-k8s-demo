package com.demo.notification.controller;

import com.demo.notification.shared.response.BaseResponse;
import com.demo.notification.shared.response.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for notification service
 * Provides health check and service status endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification service APIs")
public class NotificationController {
    
    /**
     * Health check endpoint
     * Verify that the notification service is running
     */
    @GetMapping("/health")
    @Operation(
        summary = "Health check",
        description = "Verify that the notification service is running and ready to consume messages"
    )
    public ResponseEntity<BaseResponse<String>> health() {
        return ResponseUtils.ok("Notification service is healthy and listening to RabbitMQ");
    }
    
    /**
     * Service status endpoint
     * Returns information about the notification service
     */
    @GetMapping("/status")
    @Operation(
        summary = "Service status",
        description = "Get current status and information about the notification service"
    )
    public ResponseEntity<BaseResponse<ServiceStatus>> status() {
        ServiceStatus status = new ServiceStatus(
            "Notification Service",
            "1.0.0",
            "Running",
            "Consuming messages from RabbitMQ queue: notification.queue"
        );
        return ResponseUtils.ok("Service status retrieved successfully", status);
    }
    
    /**
     * Inner class for service status response
     */
    public record ServiceStatus(
        String serviceName,
        String version,
        String status,
        String description
    ) {}
}
