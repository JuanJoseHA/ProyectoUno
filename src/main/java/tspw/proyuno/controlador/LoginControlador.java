package tspw.proyuno.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginControlador {
	
	@GetMapping("/login")
    public String login() {
        return "login"; 
    }
	
	@GetMapping("/logout")
    public String logout() {
        return "logout"; 
    }
}
