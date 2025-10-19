package tspw.proyuno.servicio;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tspw.proyuno.modelo.Medicamento;

public interface IMedicamentoServicio {
	
	List<Medicamento> buscarMedicamentos();
	
	Medicamento buscarMedicamentoId (Integer idMedicamento);
	
	void guardarMedicamento(Medicamento medicamento);
	
	String guardarFoto(MultipartFile foto);

}
