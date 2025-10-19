package tspw.proyuno.controlador;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import tspw.proyuno.modelo.Medicamento;
import tspw.proyuno.servicio.IMedicamentoServicio;

@Controller
@RequestMapping("/medicamento")
public class MedicamentoControlador {
	
	private IMedicamentoServicio servicioMed;
	
	@GetMapping("/datosMedicamento")
	public String mostrarMedicamentos(Model model) {
		
		List<Medicamento> lista=servicioMed.buscarMedicamentos();
		model.addAttribute("listaMedicamentos", lista);
		
		return "medicamentos/listaMedicamentos";
	}
	
	@GetMapping("/registroMedicamento")
	public String registrarMedicamento() {
		
		return "medicamentos/registroMedicamentos";
	}
	
	@PostMapping("/guardar")
	public String guardarMedicamento(Medicamento medicamento, @RequestParam("foto")MultipartFile foto) {
		
		String nombrefoto = servicioMed.guardarFoto(foto);
		medicamento.setFotoMedicamento(nombrefoto);
		
		servicioMed.guardarMedicamento(medicamento);
		
		return "redirect:/datosMedicamento";  
	}

}
