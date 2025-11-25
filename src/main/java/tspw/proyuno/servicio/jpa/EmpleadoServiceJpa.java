package tspw.proyuno.servicio.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tspw.proyuno.modelo.Empleado;
import tspw.proyuno.repository.EmpleadoRepository;
import tspw.proyuno.servicio.IEmpleadoServicio;

@Service
public class EmpleadoServiceJpa implements IEmpleadoServicio {

    @Autowired
    private EmpleadoRepository empleadoRepo;

    @Override
    public List<Empleado> listar() {
        return empleadoRepo.findAll();
    }

    @Override
    public Empleado buscarPorId(String id) {
        return empleadoRepo.findById(id).orElse(null);
    }

    @Override
    public Empleado guardar(Empleado emp) {
        return empleadoRepo.save(emp);
    }

    @Override
    public void eliminar(String id) {
        empleadoRepo.deleteById(id);
    }

    @Override
    public List<Empleado> buscarPorPuesto(Empleado.Puesto puesto) {
        return empleadoRepo.findByPuesto(puesto);
    }

    @Override
    public List<Empleado> buscarPorNombreContiene(String texto) {
        return empleadoRepo.findByNombreCompletoContainingIgnoreCase(texto);
    }

    @Override
    public Empleado buscarPorUsername(String username) {
        // Ligamos username con clave del empleado
        return empleadoRepo.findById(username).orElse(null);
    }
}
