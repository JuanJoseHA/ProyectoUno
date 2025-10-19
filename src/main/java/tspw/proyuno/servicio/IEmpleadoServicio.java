package tspw.proyuno.servicio;

import java.util.List;

import tspw.proyuno.modelo.Empleado;

public interface IEmpleadoServicio {
	
	List<Empleado> listar();
    Empleado buscarPorId(Integer id);
    Empleado guardar(Empleado emp);
    void eliminar(Integer id);

}
