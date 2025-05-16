package org.test.restaurant_service.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig implements WebMvcConfigurer {

    String[] PUBLIC_ORIGINS = {
            "http://localhost:63344",
            "http://localhost:63342",
            "http://127.0.0.1:63344",

            "http://localhost:63343",
            "http://127.0.0.1:63343",

            "http://localhost:9092",
            "http://127.0.0.1:9092",

    };

    String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"};

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(PUBLIC_ORIGINS)
                .allowedMethods(allowedMethods)
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
