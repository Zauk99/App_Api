// Contenido para: gestion/tareas/backend/config/WebSecurityConfig.java

package gestion.tareas.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 🛑 CRÍTICO: Deshabilitar CSRF para evitar el 403 en peticiones POST.
            .csrf(csrf -> csrf.disable())

            // 🛑 CRÍTICO: Permitir el acceso a TODAS las rutas ('/**') para deshabilitar el control de acceso.
            // Esto asegura que la lógica de tu controlador (AuthController) se ejecute sin ser interceptada.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll() // Permitir todo
            )

            // Deshabilitar formLogin y logout automáticos
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable());

        return http.build();
    }
}