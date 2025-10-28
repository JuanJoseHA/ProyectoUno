package tspw.proyuno.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tspw.proyuno.modelo.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
	
	List<Cliente> findByNombre (String nombre);
	
	List<Cliente> findByNombreContainingIgnoreCase (String texto);
	
	List<Cliente> findByEmail (String email);
	
	List<Cliente> findByEmailEndingWith (String gmail);
	
	List<Cliente> findByCreditoBetween (Double min,Double max);
	
	List<Cliente> findByCreditoGreaterThanEqual (Double min);
	
	List<Cliente> findByDestacado (Integer destacado);
	
	List<Cliente> findByNombreContainingIgnoreCaseAndCreditoGreaterThanEqual (String nombre,Double min);
	
	List<Cliente> findByFotoCliente (String fotoCliente);
	
	List<Cliente> findByDestacadoAndCreditoGreaterThanEqual (Integer destacado, Double min);
	
	List<Cliente> findTop5ByOrderByCreditoDesc ();

}

