package com.example.ingestion_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
