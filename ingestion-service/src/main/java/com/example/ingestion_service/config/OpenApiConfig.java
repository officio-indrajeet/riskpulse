package com.example.ingestion_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configures the OpenAPI/Swagger documentation exposed for this service.
 * <p>
 * Registers the {@link OpenAPI} bean consumed by springdoc-openapi to generate the
 * interactive API docs (title, description, version) for the Ingestion Service's
 * REST endpoints.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI riskPulseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RiskPulse — Ingestion Service")
                        .description("Accepts and validates incoming transactions for fraud/risk scoring")
                        .version("v1"));
    }
}
