package com.demo.notification.controller;

import com.demo.notification.shared.response.BaseResponse;
import com.demo.notification.shared.response.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for notification service
 * Provides service information endpoints
 * Note: Health check is available via Actuator at /actuator/health
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification service APIs")
public class NotificationController {
    
    /**
     * Get service information
     * Returns basic information about the notification service
     */
    @GetMapping("/info")
    @Operation(
        summary = "Get service information",
        description = "Returns basic information about the notification service"
    )
    public ResponseEntity<BaseResponse<ServiceInfo>> getInfo() {
        ServiceInfo info = new ServiceInfo(
            "Notification Service",
            "1.0.0",
            "Running",
            "Consuming messages from RabbitMQ queue: notification.queue"
        );
        return ResponseUtils.ok("Service information retrieved successfully", info);
    }
    
    /**
     * Service information response
     */
    @Schema(description = "Service information")
    public record ServiceInfo(
        @Schema(description = "Service name", example = "Notification Service")
        String serviceName,
        @Schema(description = "Service version", example = "1.0.0")
        String version,
        @Schema(description = "Current status", example = "Running")
        String status,
        @Schema(description = "Service description", example = "Consuming messages from RabbitMQ")
        String description
    ) {}
}
