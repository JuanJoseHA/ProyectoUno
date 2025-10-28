package tspw.proyuno.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tspw.proyuno.modelo.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

	@EntityGraph(attributePaths = {"idcliente", "detalles", "detalles.producto", "atendidoPor", "atendidoPor.empleado"})
	  Optional<Pedido> findWithTodoByIdpedido(Integer idpedido);
	
	@Override
    @EntityGraph(attributePaths = {"idcliente", "atendidoPor", "atendidoPor.empleado"})
    List<Pedido> findAll();
	
	long countByReservaIdservicio(Integer idservicio);
	
}
