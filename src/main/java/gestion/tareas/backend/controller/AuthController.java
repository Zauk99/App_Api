package gestion.tareas.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import gestion.tareas.backend.services.UsuarioApiClientService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    // ❗ CORRECCIÓN: Inyectamos el servicio de Usuarios
    private final UsuarioApiClientService usuarioService;

    // Constructor para inyección
    public AuthController(UsuarioApiClientService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String index() {
        // Devuelve la vista index.html
        return "index"; 
    }

    // ===================================
    // GET /iniciar-sesion: Muestra el formulario
    // ===================================
    @GetMapping("/iniciar-sesion")
    public String showLoginForm() {
        return "iniciar-sesion";
    }

    // ===================================
    // POST /iniciar-sesion: Procesa el login
    // ===================================
    @PostMapping("/iniciar-sesion")
    public String processLogin(@RequestParam String email,
            @RequestParam String contrasena,
            HttpSession session,
            Model model) {

        // El método login ahora devuelve el ID del usuario (Long) o null
        Long userId = usuarioService.login(email, contrasena);

        if (userId != null) {
            // 🟢 Éxito
            session.setAttribute("userId", userId);
            session.setAttribute("isLoggedIn", true);

            return "redirect:/usuarios";
        } else {
            // 🔴 Falla
            model.addAttribute("error", "Credenciales inválidas.");
            return "iniciar-sesion";
        }
    }
}
