package com.serhat.orderinventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderInventoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order & Inventory Management API")
                        .version("0.0.1-SNAPSHOT")
                        .description("REST API for managing products, customers, orders, inventory, payments, and order lifecycle.")
                        .contact(new Contact()
                                .name("Serhat Serçe")
                                .url("https://github.com/serhatserce")));
    }
}