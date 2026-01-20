package com.example.readycab;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
            	registry.addMapping("/**")
            	   .allowedOrigins(
            	      "https://readycab-frontend-test.vercel.app",
            	      "http://localhost:3000",
            	      "http://localhost:3001"
            	   )
            	   .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            	   .allowedHeaders("*");
            	
            }
        };
    }
}
