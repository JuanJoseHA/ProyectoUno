package tspw.proyuno.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import tspw.proyuno.modelo.Atender;
import tspw.proyuno.modelo.AtenderId;

public interface AtenderRepository extends JpaRepository<Atender, AtenderId> {

	@Modifying
    @Transactional
    @Query("delete from Atender a where a.id.idpedido = :idPedido")
    void deleteByIdIdpedido(@Param("idPedido") Integer idPedido);
	
}
