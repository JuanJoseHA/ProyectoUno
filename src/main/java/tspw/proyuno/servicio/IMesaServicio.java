package tspw.proyuno.servicio;

import java.util.List;

import tspw.proyuno.modelo.Mesa;

public interface IMesaServicio {

	List<Mesa> listar();
    Mesa buscarPorId(Integer id);
    Mesa guardar(Mesa mesa);
    void eliminar(Integer id);
	
}
