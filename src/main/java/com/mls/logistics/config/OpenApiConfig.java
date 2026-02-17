package com.mls.logistics.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI documentation configuration.
 *
 * Defines the metadata displayed in the Swagger UI header:
 * title, version, description, contact, and license.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI molsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MOLS — Multimodal Operative Logistics System API")
                        .version("1.0.0")
                        .description("""
                                Backend REST API for managing logistics operations.
                                
                                Covers the full logistics lifecycle:
                                units, warehouses, resources, stock, orders, shipments, vehicles and movement auditing.
                                
                                Architecture: Controller → Service → Repository → PostgreSQL
                                """)
                        .contact(new Contact()
                                .name("MOLS Development")
                                .email("dev@mols.com"))
                        .license(new License()
                                .name("Private")
                                .url("https://github.com/Mango420x/MOLS")));
    }
}