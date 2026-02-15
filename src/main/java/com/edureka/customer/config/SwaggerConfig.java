package com.edureka.customer.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI apiInfo() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Customer Service")
                                .description("Rest Api for Customer Service of E-Commerce")
                                .version("1.0"));
    }
}
