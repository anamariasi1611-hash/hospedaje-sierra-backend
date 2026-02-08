// Esta clase configura el CORS global para la app
package com.hospedajesierra.hospedaje_sierra_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configura la clase como componente de configuración en Spring
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Sobrescribe el método para agregar mapeos CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Aplica reglas CORS a todas las rutas
        registry.addMapping("/**")
                // Permite orígenes específicos para desarrollo
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "http://localhost:3000"
                )
                // Permite métodos HTTP comunes
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // Permite cualquier cabecera
                .allowedHeaders("*")
                // Habilita credenciales
                .allowCredentials(true)
                // Establece tiempo de caché para preflight
                .maxAge(3600);
    }
}