package com.cerebra.fileprocessor.config.openapi;

import com.cerebra.fileprocessor.config.ConfigProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Collections;
@Configuration
@AllArgsConstructor
public class OpenApiConfig {

    private final ConfigProperties configProperties;
    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .addServersItem(new Server().url(configProperties.getOpenApiServerUrl()))
                .info(new Info().title(configProperties.getOpenapiTitle())
                        .description(configProperties.getOpenapiDescription())
                        .contact(new Contact().name(configProperties.getOpenapiName()).url(configProperties.getOpenapiUrl()).email(configProperties.getOpenapiEmail()))
                        .termsOfService(configProperties.getOpenapiTermsOfService())
                        .license(new License().name(configProperties.getOpenapiLicense())))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                ).addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth", Collections.emptyList())
                );
    }
}
