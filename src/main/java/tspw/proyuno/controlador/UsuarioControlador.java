package tspw.proyuno.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tspw.proyuno.modelo.Usuario;
import tspw.proyuno.repository.PerfilRepository;
import tspw.proyuno.servicio.IPerfilServicio;
import tspw.proyuno.servicio.IUsuarioServicio;

@Controller
@RequestMapping("/usuarios")
public class UsuarioControlador {

    @Autowired
    private IUsuarioServicio serviceUsuario;
    
    @Autowired
    private IPerfilServicio servicePerfil;
    
    @Autowired
    private PerfilRepository repoPerfil; // Se usa para listar los perfiles en el formulario

    // READ - Listar todos los usuarios
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", serviceUsuario.listar());
        return "usuario/listaUsuarios"; // Debes crear esta vista
    }

    // CREATE - Mostrar formulario de registro
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
    	  Usuario u = new Usuario();
    	  model.addAttribute("usuario", u);
    	  model.addAttribute("perfiles", servicePerfil.listar()); // <--- aquí
    	  return "usuario/registroUsuario";
    	}

    // CREATE/UPDATE - Guardar o actualizar usuario
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes flash) {
        
        // La lógica de asignación de perfiles se maneja mejor en el servicio
        // Aquí solo guardamos la entidad
        serviceUsuario.guardar(usuario);
        
        flash.addFlashAttribute("ok", "Usuario guardado correctamente!");
        return "redirect:/usuarios";
    }

    // READ - Buscar por ID para edición
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes flash) {
    	  Usuario u = serviceUsuario.buscarPorId(id);
    	  if (u == null) { flash.addFlashAttribute("error","Usuario no encontrado"); return "redirect:/usuarios"; }
    	  model.addAttribute("usuario", u);
    	  model.addAttribute("perfiles", servicePerfil.listar()); // <--- aquí
    	  return "usuario/registroUsuario";
    	}
    
    // DELETE - Eliminar usuario
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Integer id, RedirectAttributes flash) {
        serviceUsuario.eliminar(id);
        flash.addFlashAttribute("ok", "Usuario eliminado correctamente!");
        return "redirect:/usuarios";
    }
}