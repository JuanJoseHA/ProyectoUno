package tspw.proyuno.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tspw.proyuno.modelo.Empleado;
import tspw.proyuno.modelo.Empleado.Puesto;
import tspw.proyuno.servicio.IEmpleadoServicio;

@Controller
@RequestMapping("/empleados")
public class EmpleadoControlador {
    @Autowired private IEmpleadoServicio service;

    @GetMapping
    public String lista(Model model, @RequestParam(value = "nombre", required = false) String nombre){
        List<Empleado> listaEmpleados;
        boolean busquedaActiva = (nombre != null && !nombre.isBlank());

        if (busquedaActiva) {
            listaEmpleados = service.buscarPorNombreContiene(nombre);
        } else {
            listaEmpleados = service.listar();
        }

        model.addAttribute("empleados", listaEmpleados);
        model.addAttribute("busquedaActiva", busquedaActiva);
        
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

    @GetMapping("/editar/{clave}")
    public String editar(@PathVariable String clave, Model model){
        Empleado e = service.buscarPorId(clave);
        if (e==null) return "redirect:/empleados";
        model.addAttribute("empleado", e);
        model.addAttribute("puestos", Puesto.values());
        return "empleado/registroEmpleado";
    }
    
    @PostMapping("/actualizar/{clave}")
    public String actualizar(@PathVariable String clave,
                             @ModelAttribute Empleado datos,
                             RedirectAttributes flash) {
        Empleado existente = service.buscarPorId(clave);
        if (existente == null) return "redirect:/empleados";

        datos.setClave(clave);
        
        existente.setNombreCompleto(datos.getNombreCompleto());
        existente.setPuesto(datos.getPuesto());

        service.guardar(existente);
        flash.addFlashAttribute("ok", "Empleado actualizado correctamente");
        return "redirect:/empleados";
    }

    @PostMapping("/eliminar/{clave}")
    public String eliminar(@PathVariable String clave, RedirectAttributes flash){
        service.eliminar(clave);
        flash.addFlashAttribute("ok", "Empleado eliminado");
        return "redirect:/empleados";
    }
}
