package tspw.proyuno.servicio.jpa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tspw.proyuno.modelo.Producto;
import tspw.proyuno.modelo.Producto.TipoP;
import tspw.proyuno.repository.ProductoRepository;
import tspw.proyuno.servicio.IProductoServicio;

@Service
@Primary

public class ProductoServiceJpa implements IProductoServicio {
	
	@Autowired
	private ProductoRepository proRepo;
	
	private final Path rutaProductos = Paths.get("C:/Producto");

	@Override
	public List<Producto> buscarProductos() {
		// TODO Auto-generated method stub
		return proRepo.findAll();
	}

	@Override
	public Producto buscarProductoId(Integer idProducto) {
		// TODO Auto-generated method stub
		
		Optional<Producto> optional=proRepo.findById(idProducto);{
			if(optional.isPresent()) {
				return optional.get();
			}
		}
		
		return null;
	}

	@Override
	public List<Producto> buscarPorTipo(TipoP tipo) {
		// TODO Auto-generated method stub
		return proRepo.findByTipo(tipo);
	}

	@Override
	public void guardarProducto(Producto producto) {
		// TODO Auto-generated method stub
		proRepo.save(producto);
	}

	@Override
	public String guardarFoto(MultipartFile foto) {
		// TODO Auto-generated method stub
		String nombreFotoProd = Path.of(foto.getOriginalFilename())
                .getFileName().toString();
		try {
			Files.copy(
					foto.getInputStream(),
					rutaProductos.resolve(nombreFotoProd),
					StandardCopyOption.REPLACE_EXISTING
			);
		    return nombreFotoProd;
		
		} catch (IOException e) {
			throw new RuntimeException("Error al guardar la foto: " + nombreFotoProd, e);
}
	}
	

	@Override
	public void eliminarPorIdProducto(Integer idProducto) {
	    proRepo.deleteById(idProducto);
	}

	@Override
	public Producto actualizarProducto(Integer idProducto, Producto datos) {
	    
		Producto p = buscarProductoId(idProducto);
	    p.setNombreprod(datos.getNombreprod());
	    p.setDescripcionprod(datos.getDescripcionprod());
	    p.setPrecio(datos.getPrecio());
	    p.setTipo(datos.getTipo());
	   
	    if (datos.getFotoprod() != null && !datos.getFotoprod().isBlank()) {
	        p.setFotoprod(datos.getFotoprod());
	    }
	    return proRepo.save(p);
	}


}
