package com.unisew.account_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Account Service API",
                version = "v1"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Gateway Server")
        }
)
public class SwaggerConfig {
}
