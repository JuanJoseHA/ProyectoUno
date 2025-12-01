package tspw.proyuno.controlador;


import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tspw.proyuno.modelo.Cliente;
import tspw.proyuno.modelo.Perfil;
import tspw.proyuno.modelo.Usuario;
import tspw.proyuno.servicio.IClienteServicio;
import tspw.proyuno.servicio.IPerfilServicio;
import tspw.proyuno.servicio.IUsuarioServicio;

@Controller
public class ClienteControlador {
	
	@Autowired
	private IClienteServicio serviceCliente;
	
    @Autowired
    private IUsuarioServicio serviceUsuario;
    @Autowired
    private IPerfilServicio servicePerfil;
	
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
        model.addAttribute("username", null); // Inicializa para evitar errores en la vista
		return "cliente/clienteRegistro";
	}
	
	@PostMapping("/guardar")
	public String guardarCliente(@ModelAttribute("clienteR") Cliente cliente, 
	                             @RequestParam(value = "foto", required = false) MultipartFile foto,
	                             @RequestParam("password") String password,
	                             @RequestParam("username") String username,
	                             Model model, 
	                             RedirectAttributes flash,
	                             Principal principal) {   // 👈 NUEVO

	    // 1. Validación de unicidad de username
		if (serviceUsuario.buscarPorUsername(username).isPresent()) {
	        model.addAttribute("error", "El nombre de usuario '" + username + "' ya está registrado. Elija otro.");
	        model.addAttribute("clienteR", cliente);
	        model.addAttribute("username", username);
	        return "cliente/clienteRegistro"; 
	    }

	    // 2. Obtener Perfil "Cliente"
	    Perfil perfilCliente = servicePerfil.buscarPorNombre("Cliente");
	    if (perfilCliente == null) {
	        flash.addFlashAttribute("error", "Error interno: El perfil 'Cliente' no existe. Notifique al Administrador.");
	        return "redirect:/registro";
	    }

	    try {
	        // 3. Guardar Cliente 
	        String nombreFoto = null;
	        if (foto != null && !foto.isEmpty()) {
	            nombreFoto = serviceCliente.guardarFoto(foto);
	        }
	        cliente.setFotoCliente(nombreFoto);
	        serviceCliente.guardarCliente(cliente);
	        
	        // 4. Crear Usuario y asignarle el rol
	        Usuario usuario = new Usuario();
	        usuario.setNombre(cliente.getNombre() + " " + cliente.getApellidos());
	        usuario.setEmail(cliente.getEmail());
	        usuario.setUsername(username);
	        usuario.setPassword(password); 
	        usuario.setEstatus(1); 
	        usuario.setPerfiles(List.of(perfilCliente));
	        
	        serviceUsuario.guardar(usuario); 
	        
	        // 5. Redirección según haya sesión o no
	        if (principal != null) {
	            // Hay usuario logueado (Admin/Cajero creando cliente)
	            flash.addFlashAttribute("ok", "Cliente registrado correctamente.");
	            return "redirect:/lista";
	        } else {
	            // Registro público
	            flash.addFlashAttribute("ok", "¡Registro exitoso! Ya puedes iniciar sesión con tu nombre de usuario.");
	            return "redirect:/login";
	        }

	    } catch (DataIntegrityViolationException ex) {
	        flash.addFlashAttribute("error", "Error de datos: Ya existe un cliente con ese email o datos duplicados. Verifique.");
	        System.err.println("DB Constraint Violation during registration: " + ex.getMostSpecificCause().getMessage());
	        return "redirect:/registro"; 
	    } catch (IllegalArgumentException ex) {
	        flash.addFlashAttribute("error", "Error de datos: La contraseña o un campo obligatorio es inválido.");
	        return "redirect:/registro";
	    } catch (Exception e) {
	        flash.addFlashAttribute("error", "Error interno al procesar el registro: " + e.getMessage());
	        System.err.println("Error during user/client registration: " + e.getMessage());
	        return "redirect:/registro"; 
	    }
	}

	
	@PostMapping("/eliminar/{id}")
	public String eliminarCliente (@PathVariable("id") int idCliente, RedirectAttributes flash) {
		serviceCliente.eliminarPorIdCliente(idCliente);
        flash.addFlashAttribute("ok", "Cliente eliminado correctamente.");
		return "redirect:/lista";
	}
	
	@GetMapping("/modificar/{id}")
	public String modificarCliente (@PathVariable("id") int idCliente,Model model) {
		
		Cliente cliente = serviceCliente.buscarPorIdCliente(idCliente);
		
		if (cliente != null && cliente.getEmail() != null) {
	        serviceUsuario.buscarPorUsername(cliente.getEmail()) 
	            .ifPresent(u -> {
	                model.addAttribute("currentUsername", u.getUsername());
	            });
	    }
		
		model.addAttribute("clienteR", cliente);
		
		model.addAttribute("titulo", "Modificar Cliente");
		
		return "cliente/clienteRegistro";
	}
	
	@PostMapping("/actualizar/{id}")
	public String actualizarCliente(@PathVariable("id") int idCliente,
	        @ModelAttribute("clienteR") Cliente datos,
	        @RequestParam(value = "foto", required = false) MultipartFile foto,
	        @RequestParam(value = "password", required = false) String password,
	        @RequestParam("username") String username, 
	        RedirectAttributes flash) {

	    // 1. Actualizar Cliente
	    if (foto != null && !foto.isEmpty()) {
	        String nombreFoto = serviceCliente.guardarFoto(foto);
	        datos.setFotoCliente(nombreFoto);
	    }
	    Cliente clienteActualizado = serviceCliente.actualizarCliente(idCliente, datos);

	    // 2. Actualizar Usuario asociado
	    try {
	        // buscarPorUsername devuelve Optional<Usuario>
	        Optional<Usuario> optUsuario = serviceUsuario.buscarPorUsername(username);

	        if (optUsuario.isPresent()) {
	            Usuario usuarioExistente = optUsuario.get();

	            usuarioExistente.setNombre(
	                clienteActualizado.getNombre() + " " + clienteActualizado.getApellidos()
	            );
	            usuarioExistente.setEmail(clienteActualizado.getEmail());

	            if (password != null && !password.isBlank()) {
	                // si usas encoder, aquí debería ir el encode:
	                // usuarioExistente.setPassword(passwordEncoder.encode(password));
	                usuarioExistente.setPassword(password);
	            }

	            serviceUsuario.guardar(usuarioExistente);
	            flash.addFlashAttribute("ok", "Cliente y usuario actualizados correctamente.");
	        } else {
	            // No se encontró el usuario con ese username
	            flash.addFlashAttribute("error",
	                "Cliente actualizado, pero no se encontró el usuario con username: " + username);
	        }

	    } catch (Exception e) {
	        flash.addFlashAttribute("error",
	            "Cliente actualizado, pero hubo un error al actualizar el usuario: " + e.getMessage());
	    }

	    return "redirect:/lista";
	}
	
}