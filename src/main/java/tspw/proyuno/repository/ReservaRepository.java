package tspw.proyuno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tspw.proyuno.modelo.Reserva;
import tspw.proyuno.modelo.Reserva.Estatus;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
	
	@EntityGraph(attributePaths = {"cliente", "mesa"})
    List<Reserva> findByEstatusOrderByFechaAscHoraAsc(Estatus estatus);
	
	@Override
    @EntityGraph(attributePaths = {"cliente", "mesa"})
    List<Reserva> findAll();

}
