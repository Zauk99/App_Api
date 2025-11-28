package gestion.tareas.backend.services;

import gestion.tareas.backend.dto.UsuarioDTO; // Asegúrate de tener este DTO en tu proyecto web
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioApiClientService {

    private final RestTemplate restTemplate;
    // La ruta base de tu API REST, por ejemplo: http://localhost:8080/api/usuarios
    private final String apiUsuariosUrl;

    public UsuarioApiClientService(RestTemplate restTemplate,
            @Value("${api.base.url}/usuarios") String apiUsuariosUrl) {
        this.restTemplate = restTemplate;
        this.apiUsuariosUrl = apiUsuariosUrl;
    }

    /**
     * Intenta autenticar enviando las credenciales al endpoint POST de la API REST (8080).
     * * @return El ID del usuario si es exitoso (200 OK), o null si falla (401 Unauthorized).
     */
    public Long login(String email, String contrasena) {
        
        // 1. Definir el endpoint de login
        // RUTA FINAL: http://localhost:8080/api/usuarios/login
        String loginUrl = apiUsuariosUrl + "/login";

        // 2. Crear el objeto de credenciales que coincide con LoginRequestDTO del 8080
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("email", email);
        credenciales.put("contrasena", contrasena); // El nombre de la clave debe coincidir

        try {
            // 3. Ejecutar POST: Delega la verificación de email y hash al servidor 8080
            ResponseEntity<Long> response = restTemplate.postForEntity(
                loginUrl, 
                credenciales, // Objeto a enviar en el cuerpo (JSON)
                Long.class    // Tipo de respuesta esperado (el ID del usuario)
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody(); // Éxito: ID de usuario devuelto por la API
            }

        } catch (HttpClientErrorException.Unauthorized e) {
            // Error 401: La API 8080 ha fallado la validación (credenciales inválidas)
            System.err.println("Login fallido: Credenciales inválidas.");
        } catch (Exception e) {
            // Otros errores (conexión, 404, etc.)
            System.err.println("Error durante la autenticación: " + e.getMessage());
        }

        return null; // Falla
    }

    // ===================================
    // READ ALL (GET /api/usuarios)
    // ===================================
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        try {
            UsuarioDTO[] usuariosArray = restTemplate.getForObject(apiUsuariosUrl, UsuarioDTO[].class);
            return usuariosArray != null ? Arrays.asList(usuariosArray) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ===================================
    // READ BY ID (GET /api/usuarios/{id})
    // ===================================
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        // RUTA: http://localhost:8080/api/usuarios/{id}
        String url = apiUsuariosUrl + "/{id}";
        try {
            return restTemplate.getForObject(url, UsuarioDTO.class, id);
        } catch (HttpClientErrorException.NotFound e) {
            return null; // El usuario no existe
        }
    }

    // ===================================
    // CREATE (POST /api/usuarios)
    // ===================================
    public UsuarioDTO crearUsuario(UsuarioDTO nuevoUsuario) {
        // RUTA: http://localhost:8080/api/usuarios
        return restTemplate.postForObject(apiUsuariosUrl, nuevoUsuario, UsuarioDTO.class);
    }

    // ===================================
    // UPDATE (PUT /api/usuarios/{id})
    // ===================================
    public void actualizarUsuario(Long id, UsuarioDTO usuarioDto) {
        // RUTA: http://localhost:8080/api/usuarios/{id}
        String url = apiUsuariosUrl + "/{id}";
        restTemplate.put(url, usuarioDto, id);
    }

    // ===================================
    // DELETE (DELETE /api/usuarios/{id})
    // ===================================
    public void eliminarUsuario(Long id) {
        // RUTA: http://localhost:8080/api/usuarios/{id}
        String url = apiUsuariosUrl + "/{id}";
        restTemplate.delete(url, id);
    }
}