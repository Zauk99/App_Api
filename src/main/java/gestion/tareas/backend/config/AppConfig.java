package gestion.tareas.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // Define un bean de RestTemplate para poder inyectarlo
    // en tus servicios.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}