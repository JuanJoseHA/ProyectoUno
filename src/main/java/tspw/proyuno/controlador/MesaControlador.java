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

import tspw.proyuno.modelo.Mesa;
import tspw.proyuno.servicio.IMesaServicio;

@Controller
@RequestMapping("/mesas")
public class MesaControlador {
    @Autowired private IMesaServicio service;

    @GetMapping
    public String lista(Model model){
        model.addAttribute("mesas", service.listar());
        return "mesa/listaMesas";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model){
        model.addAttribute("mesa", new Mesa());
        return "mesa/registroMesa";
    }

    @PostMapping
    public String guardar(@ModelAttribute Mesa mesa, RedirectAttributes flash){
        service.guardar(mesa);
        flash.addFlashAttribute("ok", "Mesa guardada");
        return "redirect:/mesas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model){
        Mesa m = service.buscarPorId(id);
        if (m==null) return "redirect:/mesas";
        model.addAttribute("mesa", m);
        return "mesa/registroMesa";
    }
    
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Mesa datos,
                             RedirectAttributes flash) {
        Mesa existente = service.buscarPorId(id);
        if (existente == null) return "redirect:/mesas";

        existente.setUbicacion(datos.getUbicacion());
        existente.setCapacidad(datos.getCapacidad());

        service.guardar(existente);
        flash.addFlashAttribute("ok", "Mesa actualizada correctamente");
        return "redirect:/mesas";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash){
        service.eliminar(id);
        flash.addFlashAttribute("ok", "Mesa eliminada");
        return "redirect:/mesas";
    }
}