package tspw.proyuno.servicio;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tspw.proyuno.modelo.Producto;

public interface IProductoServicio {
	
	List<Producto> buscarProductos();
	
	Producto buscarProductoId (Integer idProducto);
	
	List<Producto> buscarPorTipo(Producto.TipoP tipo);
	
	List<Producto> buscarPorPrecioEntre(Double min, Double max);
	
	List<Producto> buscarPorNombreYTipo(String nombre, Producto.TipoP tipo);

	void guardarProducto (Producto producto);
	
	String guardarFoto (MultipartFile producto);
	
	void eliminarPorIdProducto(Integer idProducto);
	
    Producto actualizarProducto(Integer idProducto, Producto datos);
    
    boolean estaAsociadoAPedidos(Integer idProducto);
}
