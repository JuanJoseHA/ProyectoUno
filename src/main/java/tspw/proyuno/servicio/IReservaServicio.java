package tspw.proyuno.servicio;

import java.util.List;

import org.springframework.stereotype.Service;

import tspw.proyuno.modelo.Reserva;
import tspw.proyuno.modelo.Reserva.Estatus;

public interface IReservaServicio {

	List<Reserva> listar();
    Reserva buscarPorId(Integer id);
    Reserva guardar(Reserva reserva);
    void eliminar(Integer id);
    
    List<Reserva> buscarPorEstatus(Estatus estatus);
    Reserva confirmarReserva(Integer id);
    Reserva desconfirmarReserva(Integer id);
	
}
