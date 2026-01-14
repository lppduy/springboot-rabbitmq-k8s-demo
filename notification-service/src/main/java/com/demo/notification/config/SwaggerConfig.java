package com.demo.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for Notification Service
 * Access at: http://localhost:8082/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI notificationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .description("Service for consuming order events from RabbitMQ and sending notifications")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Demo Team")
                                .email("demo@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
