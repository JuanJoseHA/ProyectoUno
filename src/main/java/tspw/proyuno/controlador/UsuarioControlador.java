package tspw.proyuno.controlador;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // Importación para manejar errores de BD
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private PerfilRepository repoPerfil;

    // READ - Listar todos los usuarios
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", serviceUsuario.listar());
        return "Usuario/listaUsuarios";
    }

    // CREATE - Mostrar formulario de registro
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Usuario u = new Usuario();
        model.addAttribute("usuario", u);
        model.addAttribute("perfiles", servicePerfil.listar());
        return "Usuario/registroUsuario";
    }

 // CREATE/UPDATE - Guardar o actualizar usuario
    @PostMapping("/guardar")
    public String guardarUsuario(
            @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            Model model,
            RedirectAttributes flash) {

        // ===== Validación de username único (antes de guardar) =====
        Optional<Usuario> optExistente = serviceUsuario.buscarPorUsername(usuario.getUsername());

        if (optExistente.isPresent()) {
            Usuario existentePorUsername = optExistente.get();

            // Si es un usuario nuevo, o es otro distinto al que estamos editando → error
            boolean esNuevo = (usuario.getId() == null);
            boolean esOtroUsuario = !esNuevo && !existentePorUsername.getId().equals(usuario.getId());

            if (esNuevo || esOtroUsuario) {
                result.rejectValue("username", "error.usuario",
                        "El nombre de usuario ya está en uso, ingrese uno diferente.");
            }
        }

        // Si hubo errores de validación, regresamos al formulario
        if (result.hasErrors()) {
            model.addAttribute("perfiles", servicePerfil.listar());
            return "Usuario/registroUsuario";
        }

        try {
            // Aquí ya sabemos que el username es único → guardamos
            serviceUsuario.guardar(usuario);
            flash.addFlashAttribute("ok", "Usuario guardado correctamente!");

            return "redirect:/usuarios";

        } catch (DataIntegrityViolationException e) {
            // Respaldo por si algo raro escapa (ej. UNIQUE en email)
            flash.addFlashAttribute("error",
                    "Error de datos en la base de datos: " + e.getMostSpecificCause().getMessage());
            return "redirect:/usuarios";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar el usuario: " + e.getMessage());
            return "redirect:/usuarios";
        }
    }

    // READ - Buscar por ID para edición
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes flash) {
        Usuario u = serviceUsuario.buscarPorId(id);
        if (u == null) {
            flash.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuarios";
        }

        // Al editar, vaciamos la contraseña para no reenviar el hash al formulario
        u.setPassword("");

        model.addAttribute("usuario", u);
        model.addAttribute("perfiles", servicePerfil.listar());
        return "Usuario/registroUsuario";
    }

    // DELETE - Eliminar usuario
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Integer id, RedirectAttributes flash) {
        serviceUsuario.eliminar(id);
        flash.addFlashAttribute("ok", "Usuario eliminado correctamente!");
        return "redirect:/usuarios";
    }
}