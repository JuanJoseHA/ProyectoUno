package tspw.proyuno.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tspw.proyuno.modelo.Perfil;
import tspw.proyuno.servicio.IPerfilServicio;

@Controller
@RequestMapping("/perfiles")
public class PerfilControlador {

    private final IPerfilServicio servicePerfil;

    public PerfilControlador(IPerfilServicio servicePerfil) {
        this.servicePerfil = servicePerfil;
    }

    // Limita los campos que se bindean para evitar choques con otros ModelAttributes
    @InitBinder("perfil")
    public void initPerfilBinder(WebDataBinder binder) {
        binder.setAllowedFields("id", "nombre");
    }

    // LISTA
    @GetMapping
    public String listarPerfiles(Model model) {
        model.addAttribute("perfiles", servicePerfil.listar());
        return "Perfil/listaPerfiles"; // vista
    }

    // FORM NUEVO
    @GetMapping("/nuevo")
    public String nuevoPerfil(Model model) {
        model.addAttribute("perfil", new Perfil());
        return "Perfil/registroPerfil"; // vista
    }

    // GUARDAR (create/update)
    @PostMapping
    public String guardarPerfil(@ModelAttribute("perfil") Perfil perfil, RedirectAttributes flash) {
        String nombre = perfil.getNombre() != null ? perfil.getNombre().trim() : "";
        if (nombre.isEmpty()) {
            flash.addFlashAttribute("error", "El nombre del perfil es obligatorio.");
            return (perfil.getId() == null) ? "redirect:/perfiles/nuevo" : "redirect:/perfiles/editar/" + perfil.getId();
        }

        // Si es alta, evita duplicados por nombre
        if (perfil.getId() == null && servicePerfil.existeNombre(nombre)) {
            flash.addFlashAttribute("error", "Ya existe un perfil con ese nombre.");
            return "redirect:/perfiles/nuevo";
        }

        perfil.setNombre(nombre);
        servicePerfil.guardar(perfil);
        flash.addFlashAttribute("ok", "Perfil guardado correctamente.");
        return "redirect:/perfiles";
    }

    // FORM EDICIÓN
    @GetMapping("/editar/{id}")
    public String editarPerfil(@PathVariable("id") Integer id, Model model, RedirectAttributes flash) {
        Perfil perfil = servicePerfil.buscarPorId(id);
        if (perfil == null) {
            flash.addFlashAttribute("error", "Perfil no encontrado.");
            return "redirect:/perfiles";
        }
        model.addAttribute("perfil", perfil);
        return "Perfil/registroPerfil";
    }

    // ELIMINAR
    @PostMapping("/eliminar/{id}")
    public String eliminarPerfil(@PathVariable("id") Integer id, RedirectAttributes flash) {
        try {
            servicePerfil.eliminar(id);
            flash.addFlashAttribute("ok", "Perfil eliminado correctamente.");
        } catch (Exception ex) {
            flash.addFlashAttribute("error", "No se pudo eliminar. Verifica dependencias (usuarios con este perfil).");
        }
        return "redirect:/perfiles";
    }
}