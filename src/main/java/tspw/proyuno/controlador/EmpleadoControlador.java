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

import tspw.proyuno.modelo.Empleado;
import tspw.proyuno.modelo.Empleado.Puesto;
import tspw.proyuno.servicio.IEmpleadoServicio;

@Controller
@RequestMapping("/empleados")
public class EmpleadoControlador {
    @Autowired private IEmpleadoServicio service;

    @GetMapping
    public String lista(Model model){
        model.addAttribute("empleados", service.listar());
        return "empleado/listaEmpleados";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model){
        Empleado e = new Empleado();
        model.addAttribute("empleado", e);
        model.addAttribute("puestos", Puesto.values());
        return "empleado/registroEmpleado";
    }

    @PostMapping
    public String guardar(@ModelAttribute Empleado empleado, RedirectAttributes flash){
        service.guardar(empleado);
        flash.addFlashAttribute("ok", "Empleado guardado");
        return "redirect:/empleados";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model){
        Empleado e = service.buscarPorId(id);
        if (e==null) return "redirect:/empleados";
        model.addAttribute("empleado", e);
        model.addAttribute("puestos", Puesto.values());
        return "empleado/registroEmpleado";
    }
    
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Empleado datos,
                             RedirectAttributes flash) {
        Empleado existente = service.buscarPorId(id);
        if (existente == null) return "redirect:/empleados";

        existente.setNombreCompleto(datos.getNombreCompleto());
        existente.setClave(datos.getClave());
        existente.setPuesto(datos.getPuesto());

        service.guardar(existente);
        flash.addFlashAttribute("ok", "Empleado actualizado correctamente");
        return "redirect:/empleados";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash){
        service.eliminar(id);
        flash.addFlashAttribute("ok", "Empleado eliminado");
        return "redirect:/empleados";
    }
}
