package com.autoshopping.stock_control.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permite origens específicas (adicione suas URLs)
        config.setAllowedOrigins(Arrays.asList(
                "http://177.70.23.73:5173",
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:8080"
        ));

        // Permite todos os headers
        config.addAllowedHeader("*");

        // Permite métodos específicos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Para permitir credenciais via cookies, informe true
        config.setAllowCredentials(true);

        // Tempo de cache para preflight requests
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
