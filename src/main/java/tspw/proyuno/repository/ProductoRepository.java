package tspw.proyuno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tspw.proyuno.modelo.Producto;
import tspw.proyuno.modelo.Producto.TipoP;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
	
	List<Producto> findByTipo (TipoP tipo);
	
	List<Producto> findByPrecioBetween(Double min, Double max);
	
	List<Producto> findByNombreprodContainingIgnoreCase(String nombreprod); 

    List<Producto> findByNombreprodContainingIgnoreCaseAndTipo(String nombreprod, TipoP tipo);

}

