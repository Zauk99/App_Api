package gestion.tareas.backend.controller;

import gestion.tareas.backend.dto.UsuarioDTO; // Asumiendo que usas el mismo DTO
import gestion.tareas.backend.services.UsuarioApiClientService;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios") // Ruta base del controlador web
public class UsuarioWebController {

    private final UsuarioApiClientService usuarioService;

    public UsuarioWebController(UsuarioApiClientService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ===================================
    // C y R: Mostrar la lista de usuarios (Página principal)
    // URL: GET /usuarios
    // ===================================

    @GetMapping
    public String listarUsuarios(Model model, HttpSession session) {

        // 🛑 IMPLEMENTACIÓN DEL CONTROL DE ACCESO MANUAL POR ID
        Long userId = (Long) session.getAttribute("userId");

        // Si no hay ID en la sesión, o el ID es 0, redirigir.
        if (userId == null || userId <= 0) {
            return "redirect:/iniciar-sesion";
        }
        // ----------------------------------------------------

        // 2. Ejecutar la lógica de negocio
        try {
            List<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
            model.addAttribute("usuarios", usuarios);

            final Long ID_ADMIN_PROTEGIDO = 3L;
            model.addAttribute("idAdminProtegido", ID_ADMIN_PROTEGIDO);
            // Usamos el ID de la sesión para mostrar contenido específico (ej:
            // "idAdminProtegido")
            model.addAttribute("currentUserId", userId);

            return "lista-usuarios";

        } catch (Exception e) {
            // En caso de fallo de la API
            model.addAttribute("error", "Error al cargar la lista de usuarios desde el servicio.");
            System.err.println("Error API: " + e.getMessage());
            return "lista-usuarios";
        }
    }

    // ===================================
    // C: Mostrar formulario para CREAR
    // URL: GET /usuarios/crear
    // ===================================
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        // Pasa un objeto UsuarioDTO vacío para el formulario
        model.addAttribute("usuario", new UsuarioDTO());
        model.addAttribute("titulo", "Crear Nuevo Usuario");
        return "formulario-usuario"; // Mapea a src/main/resources/templates/formulario-usuario.html
    }

    // ===================================
    // C: Procesar formulario para CREAR
    // URL: POST /usuarios/crear
    // ===================================
    @PostMapping("/crear")
    public String crearUsuario(@ModelAttribute UsuarioDTO usuario) {
        // CREATE: Llama al servicio para guardar
        usuarioService.crearUsuario(usuario);
        // Redirecciona a la lista
        return "redirect:/usuarios";
    }

    // ===================================
    // U: Mostrar formulario para EDITAR
    // URL: GET /usuarios/editar/{id}
    // ===================================
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        // READ BY ID: Obtiene el usuario existente
        UsuarioDTO usuarioExistente = usuarioService.obtenerUsuarioPorId(id);
        if (usuarioExistente == null) {
            return "redirect:/usuarios"; // Si no existe, vuelve a la lista
        }
        model.addAttribute("usuario", usuarioExistente);
        model.addAttribute("titulo", "Editar Usuario: " + id);
        return "formulario-usuario"; // Usa el mismo formulario
    }

    // ===================================
    // U: Procesar formulario para EDITAR
    // URL: POST /usuarios/editar/{id}
    // ===================================
    @PostMapping("/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute UsuarioDTO usuario) {
        // UPDATE: Llama al servicio para actualizar
        usuarioService.actualizarUsuario(id, usuario);
        // Redirecciona a la lista
        return "redirect:/usuarios";
    }

    // ===================================
    // D: Eliminar un usuario
    // URL: POST /usuarios/eliminar/{id} (Usamos POST por seguridad)
    // ===================================
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, Model model) {

        // ❗ REGLA DE NEGOCIO: Prohibir borrar al primer administrador
        final long ID_ADMIN_PROTEGIDO = 3L; // ❗ **Ajustar si el ID es diferente**

        if (id == ID_ADMIN_PROTEGIDO) {
            model.addAttribute("error",
                    "El administrador inicial (ID " + ID_ADMIN_PROTEGIDO + ") no puede ser eliminado.");
            // Volvemos a cargar la lista con el mensaje de error
            model.addAttribute("usuarios", usuarioService.obtenerTodosLosUsuarios());
            return "lista-usuarios";
        }

        // Continuar con la eliminación si no es el administrador protegido
        try {
            usuarioService.eliminarUsuario(id);
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo eliminar el usuario " + id + ".");
            System.err.println("Error al eliminar: " + e.getMessage());
        }

        // Redirecciona a la lista actualizada
        return "redirect:/usuarios";
    }
}