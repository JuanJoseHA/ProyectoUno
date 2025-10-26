package tspw.proyuno.servicio.jpa;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tspw.proyuno.modelo.Reserva;
import tspw.proyuno.modelo.Reserva.Estatus;
import tspw.proyuno.repository.ReservaRepository;
import tspw.proyuno.servicio.IReservaServicio;

@Service
public class ReservaServiceJpa implements IReservaServicio {
	
	@Autowired
    private ReservaRepository repo;

	@Override
	public List<Reserva> listar() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public Reserva buscarPorId(Integer id) {
		// TODO Auto-generated method stub
		return repo.findById(id).orElse(null);
	}

	@Override
	public Reserva guardar(Reserva reserva) {
	    if (reserva.getIdservicio() == null) {
	        reserva.setEstatus(Estatus.Pendiente);
	        reserva.setFecha(LocalDate.now()); // ✅ solo fecha
	        reserva.setHora(LocalTime.now());  // opcional si quieres hora por defecto
	    }
	    return repo.save(reserva);
	}


	@Override
	public void eliminar(Integer id) {
		// TODO Auto-generated method stub
		repo.deleteById(id);
	}

	@Override
	public List<Reserva> buscarPorEstatus(Estatus estatus) {
		// TODO Auto-generated method stub
		return repo.findByEstatusOrderByFechaAscHoraAsc(estatus);
	}

	@Override
	public Reserva confirmarReserva(Integer id) {
		// TODO Auto-generated method stub
		Reserva r = repo.findById(id).orElse(null);
        if (r != null) {
            r.setEstatus(Estatus.Confirmada);
            return repo.save(r);
        }
        return null;
	}

}
