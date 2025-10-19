package tspw.proyuno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import tspw.proyuno.PedidoDetalleRow;
import tspw.proyuno.modelo.DetallePedido;
import tspw.proyuno.modelo.DetallePedidoId;

public interface PedidoDetalleRepository extends JpaRepository<DetallePedido, DetallePedidoId> {
	
	@Query(value = """
		      SELECT dp.idprod           AS idprod,
		             pr.nombreprod       AS nombreprod,
		             pr.precio           AS precio,
		             dp.cantidad         AS cantidad,
		             (pr.precio*dp.cantidad) AS importe
		        FROM detalle_pedido dp
		        JOIN producto pr ON pr.idprod = dp.idprod
		       WHERE dp.idpedido = :id
		      """, nativeQuery = true)
		  List<PedidoDetalleRow> detalle(@Param("id") Integer idPedido);

	List<DetallePedido> findByIdIdpedido(Integer idpedido);
	
	@Modifying
    @Query("delete from DetallePedido d where d.id.idpedido = :idPedido")
    void deleteByPedido(@Param("idPedido") Integer idPedido);

	@Transactional
    void deleteByIdIdpedido(Integer idpedido);
}
