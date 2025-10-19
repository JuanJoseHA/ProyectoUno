package tspw.proyuno.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tspw.proyuno.modelo.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

	@EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto"})
	  Optional<Pedido> findWithTodoByIdpedido(Integer idpedido);
}
