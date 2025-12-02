// Contenido para: gestion/tareas/backend/controller/AuthController.java

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

    private final UsuarioApiClientService usuarioService;

    public AuthController(UsuarioApiClientService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Muestra el formulario de inicio de sesión
    @GetMapping("/iniciar-sesion")
    public String showLoginForm() {
        return "iniciar-sesion";
    }

    @PostMapping("/iniciar-sesion")
    public String processLogin(@RequestParam String email,
            @RequestParam String contrasena,
            HttpSession session,
            Model model) {

        // 1. Intentar hacer login (validación BCrypt y regla ID>0 en el servicio)
        Long userId = usuarioService.login(email, contrasena);

        if (userId != null && userId > 0) {
            // Éxito: Guardar ID en la sesión
            session.setAttribute("userId", userId);
            session.setAttribute("isLoggedIn", true);
            return "redirect:/usuarios";

        } else {
            // Falla: Credenciales inválidas, usuario no encontrado, o userId es 0
            
            // Limpiar cualquier sesión anterior
            session.invalidate(); 
            // Mostrar el mensaje que te sale
            model.addAttribute("error", "Credenciales inválidas."); 
            return "iniciar-sesion";
        }
    }
    
    // Método para cerrar sesión
    @GetMapping("/cerrar-sesion")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/iniciar-sesion";
    }
}