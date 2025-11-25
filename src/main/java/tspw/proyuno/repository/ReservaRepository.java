package tspw.proyuno.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tspw.proyuno.modelo.Reserva;
import tspw.proyuno.modelo.Reserva.Estatus;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
	
	@EntityGraph(attributePaths = {"cliente", "mesa"})
    List<Reserva> findByEstatusOrderByFechaAscHoraAsc(Estatus estatus);
	
	@Override
    @EntityGraph(attributePaths = {"cliente", "mesa"})
    List<Reserva> findAll();
	
	@Query("SELECT r FROM Reserva r LEFT JOIN FETCH r.cliente LEFT JOIN FETCH r.mesa WHERE " +
	           "r.fecha >= :inicio AND r.fecha <= :fin " +
	           "ORDER BY r.fecha ASC, r.hora ASC")
	    List<Reserva> findByDateRange(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // NUEVA FUNCIÓN: Busca reservas hechas por un cliente específico (por ID)
    @EntityGraph(attributePaths = {"cliente", "mesa"})
    List<Reserva> findByClienteId(Integer idCliente);
}