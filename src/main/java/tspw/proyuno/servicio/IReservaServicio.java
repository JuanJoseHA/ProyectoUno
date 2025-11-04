package tspw.proyuno.servicio;

import java.time.LocalDate;
import java.util.List;

import tspw.proyuno.modelo.Reserva;
import tspw.proyuno.modelo.Reserva.Estatus;

public interface IReservaServicio {

	List<Reserva> listar();
    Reserva buscarPorId(Integer id);
    Reserva guardar(Reserva reserva);
    void eliminar(Integer id);
    
    List<Reserva> buscarPorEstatus(Estatus estatus);
    
    List<Reserva> buscarPorRangoFechas(LocalDate inicio, LocalDate fin);
    
    Reserva confirmarReserva(Integer id);
    Reserva desconfirmarReserva(Integer id);
	
}
