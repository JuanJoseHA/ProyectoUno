package tspw.proyuno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tspw.proyuno.modelo.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

	List<Empleado> findByPuesto(Empleado.Puesto puesto);
	
	List<Empleado> findByNombreCompletoContainingIgnoreCase(String texto);
	
}
