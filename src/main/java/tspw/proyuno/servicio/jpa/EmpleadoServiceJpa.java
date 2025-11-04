package tspw.proyuno.servicio.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tspw.proyuno.modelo.Empleado;
import tspw.proyuno.modelo.Empleado.Puesto;
import tspw.proyuno.repository.EmpleadoRepository;
import tspw.proyuno.servicio.IEmpleadoServicio;

@Service
public class EmpleadoServiceJpa implements IEmpleadoServicio {
	
	@Autowired 
	private EmpleadoRepository repo;

	@Override
	public List<Empleado> listar() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public Empleado buscarPorId(String id) {
		// TODO Auto-generated method stub
		return repo.findById(id).orElse(null);
	}

	@Override
	public Empleado guardar(Empleado emp) {
		// TODO Auto-generated method stub
		return repo.save(emp);
	}

	@Override
	public void eliminar(String id) {
		// TODO Auto-generated method stub
		repo.deleteById(id);
	}

	@Override
	public List<Empleado> buscarPorPuesto(Puesto puesto) {
		// TODO Auto-generated method stub
		return repo.findByPuesto(puesto);
	}

	@Override
    public List<Empleado> buscarPorNombreContiene(String texto) {
        return repo.findByNombreCompletoContainingIgnoreCase(texto);
    }
	
	
}
