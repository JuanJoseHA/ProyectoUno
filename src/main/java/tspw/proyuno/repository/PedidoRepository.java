package tspw.proyuno.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tspw.proyuno.modelo.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

	@EntityGraph(attributePaths = {"idcliente", "detalles", "detalles.producto", "atendidoPor", "atendidoPor.empleado", "reserva", "reserva.mesa"})
	  Optional<Pedido> findWithTodoByIdpedido(Integer idpedido);
	
	@Override
    @EntityGraph(attributePaths = {"idcliente", "atendidoPor", "atendidoPor.empleado", "reserva", "reserva.mesa"})
    List<Pedido> findAll();
	
	long countByReservaIdservicio(Integer idservicio);
	
	// NUEVA FUNCIÓN: Busca pedidos atendidos por un empleado específico (Mesero)
    @Query("SELECT p FROM Pedido p JOIN p.atendidoPor a WHERE a.empleado.clave = :claveEmpleado ORDER BY p.fecha DESC")
    List<Pedido> findByEmpleadoClave(@Param("claveEmpleado") String claveEmpleado);
	
}