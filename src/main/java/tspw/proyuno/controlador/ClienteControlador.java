package tspw.proyuno.controlador;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import tspw.proyuno.modelo.Cliente;
import tspw.proyuno.servicio.IClienteServicio;

@Controller
public class ClienteControlador {
	
	@Autowired
	private IClienteServicio serviceCliente;
	
	@GetMapping("/lista")
	public String mostrarListaClientes(Model model) {
		
		List<Cliente> lista= serviceCliente.buscarTodosClientes();
		model.addAttribute("clienteLista", lista);
		
		return "ListaClientes";
	}
	
	@GetMapping("/ver/{id}")
	public String verDetalleCliente(@PathVariable("id") int idCliente, Model model) {
		Cliente cliente=serviceCliente.buscarPorIdCliente(idCliente);
		
		System.out.println("El cliente encontrado es: "+ cliente);
		model.addAttribute("cliente", cliente);
		return "cliente/detalle";
	}
	
	@GetMapping("/registro")
	public String crearCliente(Cliente cliente,Model model) {
		model.addAttribute("clienteR", new Cliente());
		return "cliente/clienteRegistro";
	}
	
	@PostMapping("/guardar")
	public String guardarCliente(Cliente cliente, @RequestParam("foto") MultipartFile foto) {
		
		String nombreFoto = serviceCliente.guardarFoto(foto);
        cliente.setFotoCliente(nombreFoto);
        
        serviceCliente.guardarCliente(cliente);
		
		System.out.println("Cliente: " + cliente);
		
		return "redirect:/lista";
		
	}
	
	@PostMapping("/eliminar/{id}")
	public String eliminarCliente (@PathVariable("id") int idCliente) {
		serviceCliente.eliminarPorIdCliente(idCliente);
		return "redirect:/lista";
	}
	
	@GetMapping("/modificar/{id}")
	public String modificarCliente (@PathVariable("id") int idCliente,Model model) {
		
		Cliente cliente = serviceCliente.buscarPorIdCliente(idCliente);
		
		model.addAttribute("clienteR", cliente);
		
		model.addAttribute("titulo", "Modificar Cliente");
		
		return "cliente/clienteRegistro";
	}
	
	@PostMapping("/actualizar/{id}")
	public String actualizarCliente(@PathVariable("id") int idCliente,
            @ModelAttribute("clienteR") Cliente datos,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {

	String nombreFoto = serviceCliente.guardarFoto(foto);
	datos.setFotoCliente(nombreFoto);
	
	serviceCliente.actualizarCliente(idCliente, datos);
	return "redirect:/lista";
	}
	
}
