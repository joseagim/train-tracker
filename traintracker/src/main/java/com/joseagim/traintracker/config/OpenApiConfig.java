package com.joseagim.traintracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI trainTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TrainTracker API")
                        .description("Train ticket management: routes, schedules, trip search and ticket buy")
                        .version("1.0"));
    }
}