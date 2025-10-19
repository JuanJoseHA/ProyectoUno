package tspw.proyuno.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tspw.proyuno.servicio.IClienteServicio;

@Controller
@RequestMapping("/busqueda")
public class ClienteBusquedaController {
	
@Autowired
private IClienteServicio serviceCliente;

@GetMapping
public String vista() { return "cliente/BusquedasCliente"; }

@GetMapping("/nombre")
public String porNombre(@RequestParam String nombre, Model model) {
    model.addAttribute("resultado",
        serviceCliente.buscarPorNombre(nombre).map(java.util.List::of).orElseGet(java.util.List::of));
    return "cliente/BusquedasCliente";
}

@GetMapping("/nombrecomo")
public String nombreLike(@RequestParam String texto, Model model) {
    model.addAttribute("resultado", serviceCliente.buscarPorNombreContiene(texto));
    return "cliente/BusquedasCliente";
}

@GetMapping("/email")
public String emailExacto(@RequestParam String email, Model model) {
    model.addAttribute("resultado",
        serviceCliente.buscarPorEmail(email).map(java.util.List::of).orElseGet(java.util.List::of));
    return "cliente/BusquedasCliente";
}

@GetMapping("/emailTerminacion")
public String emailTermina(@RequestParam(defaultValue="@gmail.com") String sufijo, Model model) {
    model.addAttribute("resultado", serviceCliente.buscarPorTerminaGmail(sufijo));
    return "cliente/BusquedasCliente";
}

@GetMapping("/creditoentre")
public String creditoEntre(@RequestParam Double min, @RequestParam Double max, Model model) {
    model.addAttribute("resultado", serviceCliente.buscarEntreValores(min, max));
    return "cliente/BusquedasCliente";
}

@GetMapping("/creditomayor")
public String creditoMayor(@RequestParam Double min, Model model) {
    model.addAttribute("resultado", serviceCliente.buscarCreditoMayor(min));
    return "cliente/BusquedasCliente";
}

@GetMapping("/destacados")
public String destacados(Model model) {
    model.addAttribute("resultado", serviceCliente.buscarPorDestacado());
    return "cliente/BusquedasCliente";
}

@GetMapping("/nombreycredito")
public String nombreYCredito(@RequestParam String nombre, @RequestParam Double min, Model model) {
    model.addAttribute("resultado", serviceCliente.buscarPorNombreyMayor(nombre, min));
    return "cliente/BusquedasCliente";
}

@GetMapping("/foto")
public String foto(@RequestParam(defaultValue="noimagen.jpg") String nombreFoto, Model model) {
    model.addAttribute("resultado", serviceCliente.buscarNoImagen(nombreFoto));
    return "cliente/BusquedasCliente";
}

@GetMapping("/destacadoscredito")
public String destacadosCredito(@RequestParam Double min, Model model) {
    model.addAttribute("resultado", serviceCliente.buscarCreditoDestacado(min));
    return "cliente/BusquedasCliente";
}

@GetMapping("/top5credito")
public String top5Credito(Model model) {
    model.addAttribute("resultado", serviceCliente.buscar5Creditos());
    return "cliente/BusquedasCliente";
}


}

