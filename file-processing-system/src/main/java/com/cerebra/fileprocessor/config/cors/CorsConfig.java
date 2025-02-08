package com.cerebra.fileprocessor.config.cors;

import com.cerebra.fileprocessor.config.ConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {
    private final ConfigProperties configProperties;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(configProperties.getCorsAllowUrls().split(","))
                .allowedMethods(configProperties.getCorsAllowMethods().split(","))
                .allowedHeaders(configProperties.getCorsAllowHeaders().split(","))
                .exposedHeaders(configProperties.getCorsAllowHeaders().split(","))
                .allowCredentials(true);
    }
}


