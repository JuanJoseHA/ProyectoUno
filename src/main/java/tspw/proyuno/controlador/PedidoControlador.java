package tspw.proyuno.controlador;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.DocumentException;

import tspw.proyuno.PedidoDetalleRow;
import tspw.proyuno.dto.PedidoItemDto;
import tspw.proyuno.modelo.Cliente;
import tspw.proyuno.modelo.Empleado.Puesto;
import tspw.proyuno.modelo.Pedido;
import tspw.proyuno.modelo.Producto;
import tspw.proyuno.modelo.Reserva;
import tspw.proyuno.modelo.Reserva.Estatus;
import tspw.proyuno.repository.AtenderRepository;
import tspw.proyuno.repository.ClienteRepository;
import tspw.proyuno.repository.PedidoDetalleRepository;
import tspw.proyuno.servicio.IClienteServicio;
import tspw.proyuno.servicio.IEmpleadoServicio;
import tspw.proyuno.servicio.IPedidoServicio;
import tspw.proyuno.servicio.IProductoServicio;
import tspw.proyuno.servicio.IReservaServicio;
import tspw.proyuno.util.PedidoPdfExporter;

@Controller
@RequestMapping("/pedidos")
public class PedidoControlador {
	
	@Autowired
	private IClienteServicio serviceCliente;
	
	@Autowired
	private IPedidoServicio servicePedido;
	
	@Autowired
	private IProductoServicio servicioProducto;
	
	@Autowired
	private PedidoDetalleRepository detalleQueryRepo;
	
	@Autowired
	private ClienteRepository clienteRepo;
	
	@Autowired
	private IEmpleadoServicio serviceEmpleado;
	
	@Autowired
	private AtenderRepository atenderRepo;
	
	@Autowired
    private IReservaServicio serviceReserva;
    
    // =========================================================================
    // NUEVA FUNCIÓN AUXILIAR: Obtener la clave del Empleado logueado
    // =========================================================================
    private String obtenerClaveEmpleado(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        try {
            // Asumiendo que el username en Spring Security es la clave del Empleado
            return auth.getName(); 
        } catch (Exception e) {
            return null;
        }
    }

    // =========================================================================
    // LISTA DE PEDIDOS - Aplicando filtro de granularidad fina
    // =========================================================================
	@GetMapping
	  public String lista(Model model, Authentication auth) { 
		
        List<Pedido> lista;

        // Comprobación de roles usando la autenticación
        boolean esMesero = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Mesero"));
        boolean puedeVerTodos = auth.getAuthorities().stream().anyMatch(a -> 
            a.getAuthority().equals("Admin") || 
            a.getAuthority().equals("Cajero") || 
            a.getAuthority().equals("Cocinero"));

        if (puedeVerTodos) {
            // Admin, Cajero, Cocinero pueden ver todos
            lista = servicePedido.mostrarPedidos();
        } else if (esMesero) {
            // Mesero solo ve sus pedidos
            String claveMesero = obtenerClaveEmpleado(auth);
            if (claveMesero != null) {
                // Llama al nuevo método para filtrar por Mesero
                lista = servicePedido.buscarPedidosPorEmpleadoClave(claveMesero);
            } else {
                lista = Collections.emptyList();
            }
        } else {
            // Otros roles
            lista = Collections.emptyList(); 
        }
		
		model.addAttribute("pedidos", lista);
	    return "Pedido/listaPedidos";
	  }
	
    // =========================================================================
    // VER DETALLE - Aplicando filtro de granularidad fina
    // =========================================================================
	@GetMapping("/{id}")
	  public String ver(@PathVariable("id") int idPedido, Model model, Authentication auth, RedirectAttributes flash) {
	    
        Pedido pedido = servicePedido.buscarPorId(idPedido);
        
        // Si el usuario es un Mesero, aplica la validación de pertenencia
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Mesero"))) {
            String claveMesero = obtenerClaveEmpleado(auth);
            if (claveMesero != null) {
                pedido = servicePedido.buscarPedidoPorIdYEmpleadoClave(idPedido, claveMesero);
            }
        } 
        // Admin, Cajero, Cocinero pasan directo (si el detalle está permitido en security)
        
	    if (pedido == null) {
	        flash.addFlashAttribute("error", "Pedido no encontrado o no autorizado.");
	        return "redirect:/pedidos";
	    }

	    List<PedidoDetalleRow> filas = detalleQueryRepo.detalle(idPedido);

	    model.addAttribute("pedido", pedido); 
	    model.addAttribute("filas", filas);   
	    return "pedido/detallePedido";
	  }

    // =========================================================================
    // EDITAR PEDIDO - Aplicando filtro de granularidad fina
    // =========================================================================
    @GetMapping("/editar/{idPedido}")
    public String editar(@PathVariable Integer idPedido, Model model, Authentication auth, RedirectAttributes flash) {
        
        Pedido pedido = servicePedido.buscarPorId(idPedido); 
        
        // Si el usuario es un Mesero, aplica la validación de pertenencia ANTES de mostrar el formulario
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Mesero"))) {
            String claveMesero = obtenerClaveEmpleado(auth);
            if (claveMesero != null) {
                // Usa el método que verifica si el mesero atiende este pedido
                pedido = servicePedido.buscarPedidoPorIdYEmpleadoClave(idPedido, claveMesero);
            }
        } 
        
        if (pedido == null) {
            flash.addFlashAttribute("error", "Pedido no encontrado o no autorizado para editar.");
            return "redirect:/pedidos";
        }

        if (pedido.getIdcliente() == null) pedido.setIdcliente(new Cliente());

        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteRepo.findAll());
        model.addAttribute("platillos", servicioProducto.buscarPorTipo(Producto.TipoP.Platillo));
        model.addAttribute("bebidas",   servicioProducto.buscarPorTipo(Producto.TipoP.Bebida));
        model.addAttribute("postres",   servicioProducto.buscarPorTipo(Producto.TipoP.Postre));
        model.addAttribute("meseros", serviceEmpleado.buscarPorPuesto(Puesto.Mesero));
        model.addAttribute("reservasConfirmadas", serviceReserva.buscarPorEstatus(Estatus.Confirmada));

        // pasa los detalles para pre-cargar el carrito
        model.addAttribute("detalles", detalleQueryRepo.findByIdIdpedido(idPedido)); 
        return "Pedido/registroPedido";
    }

	
	@PostMapping("/actualizar/{idPedido}")
	public String actualizar(@PathVariable Integer idPedido,
	                         @RequestParam("idcliente.id") Integer idcliente,     // del <select>
	                         @RequestParam("productoId") List<Integer> productoIds,
	                         @RequestParam("cantidad")   List<Integer> cantidades,
	                         @RequestParam("claveMesero") String claveMesero,
	                         @RequestParam(value="reserva.idservicio", required=false) Integer idReserva,
	                         RedirectAttributes flash) {
	    // armar DTOs
	    List<PedidoItemDto> items = new ArrayList<>();
	    for (int i=0;i<productoIds.size();i++) {
	        Integer pid = productoIds.get(i), qty = cantidades.get(i);
	        if (pid != null && qty != null && qty > 0) items.add(new PedidoItemDto(pid, qty));
	    }
	    
	    servicePedido.actualizarPedido(idPedido, idcliente, claveMesero, idReserva, items);
	    flash.addFlashAttribute("ok", "Pedido actualizado");
	    return "redirect:/pedidos/" + idPedido;
	}
	
	// Métodos restantes (crear, guardar, eliminar, pdf)
    // ... (rest of the file content unchanged)
	@GetMapping("/nuevo")
	  public String nuevo(Model model,
			  @RequestParam(value = "clienteId", required = false) Integer clienteId,
              @RequestParam(value = "reservaId", required = false) Integer reservaId) {
		
		Pedido p = new Pedido();

        // 1. Pre-cargar Cliente si el parámetro existe
        if (clienteId != null) {
            Cliente cliente = serviceCliente.buscarPorIdCliente(clienteId);
            if (cliente != null) {
                p.setIdcliente(cliente);
            }
        }

        // 2. Pre-cargar Reserva si el parámetro existe
        if (reservaId != null) {
            Reserva reserva = serviceReserva.buscarPorId(reservaId);
            if (reserva != null) {
                p.setReserva(reserva);
            }
        }
		
	    model.addAttribute("pedido", p);
	    model.addAttribute("platillos", servicioProducto.buscarPorTipo(Producto.TipoP.Platillo));
	    model.addAttribute("bebidas",   servicioProducto.buscarPorTipo(Producto.TipoP.Bebida));
	    model.addAttribute("postres",   servicioProducto.buscarPorTipo(Producto.TipoP.Postre));
	    model.addAttribute("clientes", serviceCliente.buscarTodosClientes());
	    model.addAttribute("meseros", serviceEmpleado.buscarPorPuesto(Puesto.Mesero));
	    model.addAttribute("reservasConfirmadas", serviceReserva.buscarPorEstatus(Estatus.Confirmada));
	    return "Pedido/registroPedido";
	  }
	
	@PostMapping
	public String crear(@RequestParam("idcliente.id") Integer idcliente,
	                    @RequestParam("productoId") List<Integer> productoIds,
	                    @RequestParam("cantidad")   List<Integer> cantidades,
	                    @RequestParam("claveMesero") String claveMesero,
	                    @RequestParam(value="reserva.idservicio", required=false) Integer idReserva,
	                    RedirectAttributes flash) {

	    List<PedidoItemDto> items = new ArrayList<>();
	    for (int i = 0; i < productoIds.size(); i++) {
	        Integer pid = productoIds.get(i);
	        Integer qty = cantidades.get(i);
	        if (pid != null && qty != null && qty > 0) {
	            items.add(new PedidoItemDto(pid, qty));
	        }
	    }

	    Pedido p = servicePedido.crearPedido(idcliente, claveMesero, idReserva, items);
	    flash.addFlashAttribute("ok", "Pedido creado #" + p.getIdpedido());
	    return "redirect:/pedidos/" + p.getIdpedido();
	}


	
	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable("id") int idPedido, RedirectAttributes flash) {
	    servicePedido.eliminarPedido(idPedido);
	    flash.addFlashAttribute("ok", "Pedido eliminado correctamente");
	    return "redirect:/pedidos";
	}
	
	@GetMapping("/{id}/pdf")
	public ResponseEntity<byte[]> generarPdf(@PathVariable("id") int idPedido) {
	    Pedido pedido = servicePedido.buscarPorId(idPedido);
	    if (pedido == null) {
	        return ResponseEntity.notFound().build();
	    }

	    List<PedidoDetalleRow> filas = detalleQueryRepo.detalle(idPedido);

	    try {
	        ByteArrayOutputStream pdfStream = PedidoPdfExporter.export(pedido, filas);

	        String filename = "Ticket_Pedido_" + idPedido + ".pdf";

	        return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(pdfStream.toByteArray());

	    } catch (DocumentException e) {
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().build();
	    }
	}
}