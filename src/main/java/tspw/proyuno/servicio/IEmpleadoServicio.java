package tspw.proyuno.servicio;

import java.util.List;

import tspw.proyuno.modelo.Empleado;

public interface IEmpleadoServicio {
	
	List<Empleado> listar();
    Empleado buscarPorId(String id);
    Empleado guardar(Empleado emp);
    void eliminar(String id);
    List<Empleado> buscarPorPuesto(Empleado.Puesto puesto);
    
    List<Empleado> buscarPorNombreContiene(String texto);

}

