package com.turkcell.etradedemo7.core.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Trade Demo API")
                        .version("1.0")
                        .description("Turkcell E-Commerce Demo Application REST API")
                        .contact(new Contact()
                                .name("Turkcell")
                        )
                );
    }
}
