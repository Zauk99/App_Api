package gestion.tareas.backend.services;

import gestion.tareas.backend.dto.UsuarioDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioApiClientService {

    private final RestTemplate restTemplate;
    private final String apiUsuariosUrl = "http://localhost:8080/api/usuarios";
    private final PasswordEncoder passwordEncoder;

    // Constructor con inyección de RestTemplate y PasswordEncoder
    public UsuarioApiClientService(RestTemplate restTemplate, PasswordEncoder passwordEncoder) {
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    // ===================================
    // R: READ ALL (GET /api/usuarios)
    // ===================================
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        try {
            // URL: http://localhost:8080/api/usuarios
            UsuarioDTO[] usuariosArray = restTemplate.getForObject(apiUsuariosUrl, UsuarioDTO[].class);
            return usuariosArray != null ? Arrays.asList(usuariosArray) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ===================================
    // R: READ BY ID (GET /api/usuarios/{id})
    // ===================================
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        // URL: http://localhost:8080/api/usuarios/{id}
        String url = apiUsuariosUrl + "/{id}";
        try {
            return restTemplate.getForObject(url, UsuarioDTO.class, id);
        } catch (HttpClientErrorException.NotFound e) {
            return null; // El usuario no existe (404)
        }
    }

    // ===================================
    // C: CREATE (POST /api/usuarios)
    // ===================================
    public void crearUsuario(UsuarioDTO nuevoUsuario) {
        // URL: http://localhost:8080/api/usuarios
        try {
            // El API espera un UsuarioDTO en el cuerpo de la petición.
            restTemplate.postForObject(apiUsuariosUrl, nuevoUsuario, UsuarioDTO.class);
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            throw new RuntimeException("Fallo al crear usuario en la API.");
        }
    }

    // ===================================
    // U: UPDATE (PUT /api/usuarios/{id})
    // ===================================
    public void actualizarUsuario(Long id, UsuarioDTO usuarioDto) {
        // URL: http://localhost:8080/api/usuarios/{id}
        String url = apiUsuariosUrl + "/{id}";

        try {
            // Se usa PUT y se pasan el objeto DTO y el ID como variable de la ruta.
            restTemplate.put(url, usuarioDto, id);
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario " + id + ": " + e.getMessage());
            throw new RuntimeException("Fallo al actualizar usuario en la API.");
        }
    }

    // ===================================
    // D: DELETE (DELETE /api/usuarios/{id})
    // ===================================
    public void eliminarUsuario(Long id) {
        // URL: http://localhost:8080/api/usuarios/{id}
        String url = apiUsuariosUrl + "/{id}";

        try {
            // Se usa DELETE y se pasa el ID como variable de la ruta.
            restTemplate.delete(url, id);
        } catch (HttpClientErrorException.NotFound e) {
            System.err.println("El usuario a eliminar (" + id + ") no fue encontrado.");
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario " + id + ": " + e.getMessage());
            throw new RuntimeException("Fallo al eliminar usuario en la API.");
        }
    }

    /**
     * Intenta autenticar a un usuario usando email y contraseña.
     * Realiza la comparación de contraseñas con BCrypt.
     * 
     * @param email      El email introducido por el usuario.
     * @param contrasena La contraseña en texto plano introducida por el usuario.
     * @return El ID del usuario si las credenciales son válidas, o null en caso
     *         contrario.
     */
    public Long login(String email, String contrasena) {
        Optional<UsuarioDTO> usuarioOpt = obtenerUsuarioPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        UsuarioDTO usuario = usuarioOpt.get();
        String storedHashedPassword = usuario.getContrasena();

        // 🛑 DEBE USAR PasswordEncoder.matches() para que funcione con BCrypt
        boolean isMatch = passwordEncoder.matches(contrasena, storedHashedPassword);

        if (isMatch) {
            // Devuelve null si el ID es 0 (según tu requisito)
            if (usuario.getId() == 0L) {
                return null;
            }
            return usuario.getId();
        } else {
            return null; // Contraseña incorrecta
        }
    }

    /**
     * Obtiene un usuario de la API del 8080 por su dirección de email.
     * 🛑 URL CORREGIDA para usar /buscar-email?email={email}
     */
    public Optional<UsuarioDTO> obtenerUsuarioPorEmail(String email) {
        try {
            // 🛑 Uso de UriComponentsBuilder para manejar el RequestParam
            String url = UriComponentsBuilder.fromUriString(apiUsuariosUrl)
                    .path("/buscar-email") // Ruta de tu API
                    .queryParam("email", email) // Parámetro de consulta
                    .toUriString(); 
            
            UsuarioDTO usuario = restTemplate.getForObject(url, UsuarioDTO.class);
            
            return Optional.ofNullable(usuario);
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Usuario con email " + email + " no encontrado (404).");
            return Optional.empty();
        } catch (Exception e) {
            // ❗ Mantenemos el logging del error por si persiste el 403
            System.err.println("Error al buscar usuario por email en la API: " + e.getMessage());
            return Optional.empty();
        }
    }
}