package gestion.tareas.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        // Asumiendo que tu archivo se llama 'iniciar-sesion.html'
        return "iniciar-sesion"; 
    }
}