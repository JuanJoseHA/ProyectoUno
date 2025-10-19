package tspw.proyuno.controlador;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class InicioControlador {
	
	
	@GetMapping("/home")
	public String mostrarInicio(Model model) {
		return "Inicio";
		
	}
	
	@GetMapping("/cliente")
	public String datosCliente(Model model) {
		
		String nombre = "Juan Jose";
		String correo = "juanha@gmail.com";
		double creditos = 13000;
		boolean vigente = true;
		Date fechaActual = new Date();
		
		model.addAttribute("nombre", nombre);
		model.addAttribute("correo", correo);
		model.addAttribute("credito", creditos);
		model.addAttribute("vigente", vigente);
		model.addAttribute("fecha", fechaActual);
		
		return "Cliente";
	}
	
	@GetMapping("/listadoClientes")
	public String mostrarListaClientes(Model model) {
		//Creacion de una instancia de tipo lista
		
		List<String> listac = new LinkedList<String>();
		
		//Agregar Elementos
		listac.add("Andrea Ramírez");
		listac.add("Miguel Nieto");
		listac.add("Samantha Garcia");
		
		model.addAttribute("clientes", listac);
		
		return "ListaClientes";
	}

}
