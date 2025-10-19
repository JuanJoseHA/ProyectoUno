package tspw.proyuno.servicio.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tspw.proyuno.modelo.Medicamento;
import tspw.proyuno.servicio.IMedicamentoServicio;

@Service
public class MedicamentoServicioImp implements IMedicamentoServicio {

	private List<Medicamento> listaMedicamentos;
	
	Path rutaFotos = Paths.get("src/main/resources/static/imagenes/medicamentos");
	
	public void MedicamentoServiceImp() {
		
		listaMedicamentos = new LinkedList<>();
		
		Medicamento M1 = new Medicamento();
		M1.setId(1);
		M1.setCodigo("001");
		M1.setNombre("Paracetamol");
		M1.setPrecioCompra(70);
		M1.setPrecioVenta(90);
		M1.setStock(10);
		M1.setUnidad("20");
		M1.setFotoMedicamento("cafe.jpg");
		
		listaMedicamentos.add(M1);
	}

	@Override
	public List<Medicamento> buscarMedicamentos() {
		// TODO Auto-generated method stub
		return listaMedicamentos;
	}

	@Override
	public Medicamento buscarMedicamentoId(Integer idMedicamento) {
        for (Medicamento med : listaMedicamentos) {
            if (med.getId()==idMedicamento) {
                return med;
            }
        }
        return null;
	}

	@Override
	public void guardarMedicamento(Medicamento medicamento) {
		listaMedicamentos.add(medicamento);
	}

	@Override
	public String guardarFoto(MultipartFile foto) {
	    if (foto == null || foto.isEmpty()) {
	        return null;
	    }

	    String nombreArchivo = foto.getOriginalFilename();

	    try {
	        Files.copy(
	            foto.getInputStream(),
	            rutaFotos.resolve(nombreArchivo),
	            StandardCopyOption.REPLACE_EXISTING
	        );
	        return nombreArchivo;
	    } catch (IOException e) {
	        throw new RuntimeException("Error al guardar la foto: " + nombreArchivo, e);
	    }
	}

}
