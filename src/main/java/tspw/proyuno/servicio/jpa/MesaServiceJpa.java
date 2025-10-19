package tspw.proyuno.servicio.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tspw.proyuno.modelo.Mesa;
import tspw.proyuno.repository.MesaRepository;
import tspw.proyuno.servicio.IMesaServicio;

@Service
public class MesaServiceJpa implements IMesaServicio {

	@Autowired 
	private MesaRepository repo;
	
	@Override
	public List<Mesa> listar() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public Mesa buscarPorId(Integer id) {
		// TODO Auto-generated method stub
		return repo.findById(id).orElse(null);
	}

	@Override
	public Mesa guardar(Mesa mesa) {
		// TODO Auto-generated method stub
		return repo.save(mesa);
	}

	@Override
	public void eliminar(Integer id) {
		// TODO Auto-generated method stub
		repo.deleteById(id);
	}

}
