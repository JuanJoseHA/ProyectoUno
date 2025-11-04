package tspw.proyuno.controlador;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tspw.proyuno.modelo.Reserva;
import tspw.proyuno.modelo.Reserva.Estatus;
import tspw.proyuno.servicio.IClienteServicio;
import tspw.proyuno.servicio.IMesaServicio;
import tspw.proyuno.servicio.IReservaServicio;

@Controller
@RequestMapping("/reservas")
public class ReservaControlador {
	
	@Autowired private IReservaServicio serviceReserva;
    @Autowired private IClienteServicio serviceCliente;
    @Autowired private IMesaServicio serviceMesa;
    
    @GetMapping
    public String lista(Model model,
            @RequestParam(value="inicio", required = false) LocalDate inicio,
            @RequestParam(value="fin", required = false) LocalDate fin) {


        List<Reserva> listaBase = (inicio != null)
                ? serviceReserva.buscarPorRangoFechas(inicio, fin)
                : serviceReserva.listar();

        model.addAttribute("busquedaActiva", inicio != null);

        List<Reserva> pendientes = listaBase.stream()
                .filter(r -> r.getEstatus() == Estatus.Pendiente)
                .collect(java.util.stream.Collectors.toList());

        List<Reserva> confirmadas = listaBase.stream()
                .filter(r -> r.getEstatus() == Estatus.Confirmada)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("pendientes", pendientes);
        model.addAttribute("confirmadas", confirmadas);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fin", fin);

        return "reserva/listaReservas";
    }
    
    @GetMapping("/nuevo")
    public String nuevo(Model model){
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("clientes", serviceCliente.buscarTodosClientes());
        model.addAttribute("mesas", serviceMesa.listar());
        return "reserva/registroReserva";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("reserva") Reserva reserva,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", serviceCliente.buscarTodosClientes());
            model.addAttribute("mesas", serviceMesa.listar());
            model.addAttribute("estatuses", Reserva.Estatus.values());
            return "reserva/registroReserva";
        }
        serviceReserva.guardar(reserva);
        flash.addFlashAttribute("ok", "Reserva guardada.");
        return "redirect:/reservas";
    }

    
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model){
        Reserva r = serviceReserva.buscarPorId(id);
        if (r == null) return "redirect:/reservas";
        
        model.addAttribute("reserva", r);
        model.addAttribute("clientes", serviceCliente.buscarTodosClientes());
        model.addAttribute("mesas", serviceMesa.listar());
        model.addAttribute("estatuses", Estatus.values());
        return "reserva/registroReserva";
    }
    
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute Reserva datos, RedirectAttributes flash) {
        Reserva existente = serviceReserva.buscarPorId(id);
        if (existente == null) return "redirect:/reservas";
        
        existente.setCliente(datos.getCliente());
        existente.setMesa(datos.getMesa());
        existente.setFecha(datos.getFecha());
        existente.setHora(datos.getHora());
        existente.setEstatus(datos.getEstatus());

        serviceReserva.guardar(existente);
        flash.addFlashAttribute("ok", "Reserva #" + id + " actualizada.");
        return "redirect:/reservas";
    }
    
    @PostMapping("/confirmar/{id}")
    public String confirmar(@PathVariable Integer id, RedirectAttributes flash){
        serviceReserva.confirmarReserva(id);
        flash.addFlashAttribute("ok", "Reserva #" + id + " Confirmada.");
        return "redirect:/reservas";
    }

    @PostMapping("/eliminar/{id}") // Esto actúa como 'Cancelar'
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash){
        serviceReserva.eliminar(id);
        flash.addFlashAttribute("ok", "Reserva #" + id + " Cancelada/Eliminada.");
        return "redirect:/reservas";
    }
    
    @PostMapping("/desconfirmar/{id}")
    public String desconfirmar(@PathVariable Integer id, RedirectAttributes flash){
    	try {
            serviceReserva.desconfirmarReserva(id);
            flash.addFlashAttribute("ok", "Reserva #" + id + " Desconfirmada (Pendiente).");
        } catch (IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al desconfirmar la reserva: " + e.getMessage());
        }
        return "redirect:/reservas";
    }

}
