package gestion.tareas.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 🟢 1. EXCLUIR RECURSOS ESTÁTICOS DE FORMA ESTÁNDAR
                        // Esto asegura que /css, /js, /images/ etc. no requieran autenticación.
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // 2. Permitir acceso a la raíz, login, registro y la API (si es pública)
                        .requestMatchers("/", "/login", "/registro", "/api/**").permitAll()

                        // 3. Proteger todas las demás peticiones
                        .anyRequest().authenticated())

                // 2. Configuración de Login: Redirige a /login cuando se necesita autenticación
                .formLogin(form -> form
                        .loginPage("/login") // ❗ URL de la página de inicio de sesión
                        .defaultSuccessUrl("/usuarios", true) // Redirección tras login exitoso
                        .permitAll())

                // 3. Configuración de Logout (Cierre de Sesión)
                .logout(logout -> logout
                        .logoutUrl("/cerrar-sesion") // URL a la que el formulario POST debe apuntar
                        .logoutSuccessUrl("/login?logout") // Redirige al login tras cerrar sesión
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        // 4. CSRF: Se mantiene activo por defecto para proteger el formulario POST de
        // logout

        return http.build();
    }
}
