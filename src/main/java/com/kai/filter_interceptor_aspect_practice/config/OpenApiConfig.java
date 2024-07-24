package com.kai.filter_interceptor_aspect_practice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Practice Filete, Interceptor, Aspect",
                version = "1.0"
//                description = "Practice Filete, Interceptor, Aspect"
        )
)
public class OpenApiConfig {
}
