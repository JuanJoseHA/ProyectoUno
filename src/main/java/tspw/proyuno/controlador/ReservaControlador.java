package tspw.proyuno.controlador;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tspw.proyuno.modelo.Cliente;
import tspw.proyuno.modelo.Reserva;
import tspw.proyuno.modelo.Reserva.Estatus;
import tspw.proyuno.modelo.Usuario;
import tspw.proyuno.servicio.IClienteServicio;
import tspw.proyuno.servicio.IMesaServicio;
import tspw.proyuno.servicio.IReservaServicio;
import tspw.proyuno.servicio.IUsuarioServicio;

@Controller
@RequestMapping("/reservas")
public class ReservaControlador {
	
    @Autowired private IReservaServicio serviceReserva;
    @Autowired private IClienteServicio serviceCliente;
    @Autowired private IMesaServicio serviceMesa;
    @Autowired private IUsuarioServicio serviceUsuario;   // 👈 NUEVO

    // =========================================================================
    // AUXILIAR: Obtener el Cliente logueado a partir del Usuario
    // =========================================================================
    private Cliente obtenerClienteLogueado(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        try {
            Usuario usuario = serviceUsuario.buscarPorUsername(auth.getName());
            if (usuario == null || usuario.getEmail() == null) return null;

            // Usamos el método que devuelve List<Cliente>
            List<Cliente> coincidencias = serviceCliente.buscarPorEmail(usuario.getEmail());
            if (coincidencias == null || coincidencias.isEmpty()) {
                return null;
            }
            return coincidencias.get(0); // asumimos email único

        } catch (Exception e) {
            return null;
        }
    }

    
    // =========================================================================
    // LISTA DE RESERVAS
    // =========================================================================
    @GetMapping
    public String lista(Model model, Authentication auth, 
            @RequestParam(value="inicio", required = false) LocalDate inicio,
            @RequestParam(value="fin", required = false) LocalDate fin) {

        List<Reserva> listaBase;
        
        boolean esCliente = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Cliente"));

        boolean puedeVerTodas = auth.getAuthorities().stream().anyMatch(a -> 
            a.getAuthority().equals("Admin") || 
            a.getAuthority().equals("Cajero"));

        if (puedeVerTodas) {
            // Admin y Cajero: todas las reservas, con o sin filtro
            listaBase = (inicio != null)
                ? serviceReserva.buscarPorRangoFechas(inicio, fin)
                : serviceReserva.listar();
            model.addAttribute("busquedaActiva", inicio != null);

        } else if (esCliente) {
            // Cliente: solo sus reservas
            Cliente cli = obtenerClienteLogueado(auth);
            if (cli != null) {
                listaBase = serviceReserva.buscarReservasPorClienteId(cli.getId());
            } else {
                listaBase = Collections.emptyList();
            }
            model.addAttribute("busquedaActiva", false); 
            
        } else {
            // Otros roles (Cocinero/Mesero) quedan vacíos (ya están filtrados por seguridad)
            listaBase = Collections.emptyList(); 
        }

        List<Reserva> pendientes = listaBase.stream()
                .filter(r -> r.getEstatus() == Estatus.Pendiente)
                .toList();

        List<Reserva> confirmadas = listaBase.stream()
                .filter(r -> r.getEstatus() == Estatus.Confirmada)
                .toList();

        model.addAttribute("pendientes", pendientes);
        model.addAttribute("confirmadas", confirmadas);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fin", fin);

        return "reserva/listaReservas";
    }
    
    // =========================================================================
    // CREAR RESERVA - Cliente ya viene precargado si el rol es Cliente
    // =========================================================================
    @GetMapping("/nuevo")
    public String nuevo(Model model, Authentication auth){
        Reserva r = new Reserva();
        
        boolean esCliente = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Cliente"));

        if (esCliente) {
            Cliente cli = obtenerClienteLogueado(auth);
            if (cli != null) {
                r.setCliente(cli);  // 👈 se preasigna el cliente
            }
        }
        
        model.addAttribute("reserva", r);
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

    @PostMapping("/eliminar/{id}")
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
