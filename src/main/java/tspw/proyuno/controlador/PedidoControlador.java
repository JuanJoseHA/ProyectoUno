package tspw.proyuno.controlador;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tspw.proyuno.PedidoDetalleRow;
import tspw.proyuno.dto.PedidoItemDto;
import tspw.proyuno.modelo.Cliente;
import tspw.proyuno.modelo.Empleado.Puesto;
import tspw.proyuno.modelo.Pedido;
import tspw.proyuno.modelo.Producto;
import tspw.proyuno.repository.AtenderRepository;
import tspw.proyuno.repository.ClienteRepository;
import tspw.proyuno.repository.PedidoDetalleRepository;
import tspw.proyuno.servicio.IClienteServicio;
import tspw.proyuno.servicio.IEmpleadoServicio;
import tspw.proyuno.servicio.IPedidoServicio;
import tspw.proyuno.servicio.IProductoServicio;

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

	@GetMapping
	  public String lista(Model model) {
		
		List<Pedido> lista= servicePedido.mostrarPedidos();
		model.addAttribute("pedidos", lista);
		
	    return "Pedido/listaPedidos";
	  }
	
	@GetMapping("/nuevo")
	  public String nuevo(Model model) {
	    model.addAttribute("pedido", new Pedido());
	    model.addAttribute("platillos", servicioProducto.buscarPorTipo(Producto.TipoP.Platillo));
	    model.addAttribute("bebidas",   servicioProducto.buscarPorTipo(Producto.TipoP.Bebida));
	    model.addAttribute("postres",   servicioProducto.buscarPorTipo(Producto.TipoP.Postre));
	    model.addAttribute("clientes", serviceCliente.buscarTodosClientes());
	    model.addAttribute("meseros", serviceEmpleado.buscarPorPuesto(Puesto.Mesero));
	    return "Pedido/registroPedido";
	  }
	
	@PostMapping
	public String crear(@RequestParam("idcliente.id") Integer idcliente,
	                    @RequestParam("productoId") List<Integer> productoIds,
	                    @RequestParam("cantidad")   List<Integer> cantidades,
	                    @RequestParam("claveMesero") String claveMesero,
	                    RedirectAttributes flash) {

	    List<PedidoItemDto> items = new ArrayList<>();
	    for (int i = 0; i < productoIds.size(); i++) {
	        Integer pid = productoIds.get(i);
	        Integer qty = cantidades.get(i);
	        if (pid != null && qty != null && qty > 0) {
	            items.add(new PedidoItemDto(pid, qty));
	        }
	    }

	    Pedido p = servicePedido.crearPedido(idcliente, claveMesero, items);
	    flash.addFlashAttribute("ok", "Pedido creado #" + p.getIdpedido());
	    return "redirect:/pedidos/" + p.getIdpedido();
	}


	
	@PostMapping("/eliminar/{id}")
	public String eliminar(@PathVariable("id") int idPedido, RedirectAttributes flash) {
	    servicePedido.eliminarPedido(idPedido);
	    flash.addFlashAttribute("ok", "Pedido eliminado correctamente");
	    return "redirect:/pedidos";
	}
	
	@GetMapping("/{id}")
	  public String ver(@PathVariable("id") int idPedido, Model model) {
	    Pedido pedido = servicePedido.buscarPorId(idPedido);
	    if (pedido == null) return "redirect:/pedidos";

	    List<PedidoDetalleRow> filas = detalleQueryRepo.detalle(idPedido);

	    model.addAttribute("pedido", pedido); // id, idcliente, fecha, total
	    model.addAttribute("filas", filas);   // productos, precio, cantidad, importe
	    return "pedido/detallePedido";
	  }

	// GET: muestra el mismo form pero cargado con datos
	@GetMapping("/editar/{idPedido}")
	public String editar(@PathVariable Integer idPedido, Model model) {
	    Pedido pedido = servicePedido.buscarPorId(idPedido); // o un findWithTodo si cargas detalles LAZY
	    if (pedido == null) return "redirect:/pedidos";

	    if (pedido.getIdcliente() == null) pedido.setIdcliente(new Cliente());

	    model.addAttribute("pedido", pedido);
	    model.addAttribute("clientes", clienteRepo.findAll());
	    model.addAttribute("platillos", servicioProducto.buscarPorTipo(Producto.TipoP.Platillo));
	    model.addAttribute("bebidas",   servicioProducto.buscarPorTipo(Producto.TipoP.Bebida));
	    model.addAttribute("postres",   servicioProducto.buscarPorTipo(Producto.TipoP.Postre));
	    model.addAttribute("meseros", serviceEmpleado.buscarPorPuesto(Puesto.Mesero));

	    // pasa los detalles para pre-cargar el carrito
	    model.addAttribute("detalles", detalleQueryRepo.findByIdIdpedido(idPedido)); // o pedido.getDetalles()
	    return "Pedido/registroPedido";
	}

	// POST: actualizar (mismo form, distinto action)
	@PostMapping("/actualizar/{idPedido}")
	public String actualizar(@PathVariable Integer idPedido,
	                         @RequestParam("idcliente.id") Integer idcliente,     // del <select>
	                         @RequestParam("productoId") List<Integer> productoIds,
	                         @RequestParam("cantidad")   List<Integer> cantidades,
	                         @RequestParam("claveMesero") String claveMesero,
	                         RedirectAttributes flash) {
	    // armar DTOs
	    List<PedidoItemDto> items = new ArrayList<>();
	    for (int i=0;i<productoIds.size();i++) {
	        Integer pid = productoIds.get(i), qty = cantidades.get(i);
	        if (pid != null && qty != null && qty > 0) items.add(new PedidoItemDto(pid, qty));
	    }
	    
	    servicePedido.actualizarPedido(idPedido, idcliente, claveMesero, items);
	    flash.addFlashAttribute("ok", "Pedido actualizado");
	    return "redirect:/pedidos/" + idPedido;
	}

	
}
