package de.ait.g_67_shop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Application Shop",
                description = "Application for various operations with Stores, Customers and Products",
                version = "1.0.0",
                contact = @Contact(
                        name = "Sergej",
                        email = "bmw325d@web.de"
                )
        )
)
public class SwaggerConfig {
}
