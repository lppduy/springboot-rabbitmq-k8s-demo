package com.demo.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for Order Service
 * Access at: http://localhost:8081/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("REST API for managing orders and publishing events to RabbitMQ")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Demo Team")
                                .email("demo@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
