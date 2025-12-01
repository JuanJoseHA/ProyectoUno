package tspw.proyuno.servicio.jpa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tspw.proyuno.modelo.Producto;
import tspw.proyuno.modelo.Producto.TipoP;
import tspw.proyuno.repository.PedidoDetalleRepository;
import tspw.proyuno.repository.ProductoRepository;
import tspw.proyuno.servicio.IProductoServicio;

@Service
@Primary

public class ProductoServiceJpa implements IProductoServicio {
	
	@Autowired
	private ProductoRepository proRepo;
	
	@Autowired 
	private PedidoDetalleRepository detalleRepo;
	
	@Value("${PATH_PRODUCTO_IMAGENES:/app/imagenes_subidas}")
    private String rutaProductosString;

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
	public List<Producto> buscarPorPrecioEntre(Double min, Double max) {
	    return proRepo.findByPrecioBetween(min, max);
	}
	
	@Override
    public List<Producto> buscarPorNombreYTipo(String nombre, TipoP tipo) {
        if (nombre == null || nombre.isBlank()) {
            if (tipo != null) {
                return proRepo.findByTipo(tipo);
            } else {
                return proRepo.findAll();
            }
        }

        if (tipo != null) {
            return proRepo.findByNombreprodContainingIgnoreCaseAndTipo(nombre, tipo);
        } else {
            return proRepo.findByNombreprodContainingIgnoreCase(nombre);
        }
    }

	@Override
	public void guardarProducto(Producto producto) {
		// TODO Auto-generated method stub
		proRepo.save(producto);
	}

	@Override
	public String guardarFoto(MultipartFile foto) {
		// TODO Auto-generated method stub
		
		if (foto == null || foto.isEmpty()) {
	        return "noimagen.png";
	    }
		
		String nombreFotoProd = Path.of(foto.getOriginalFilename()).getFileName().toString();
	    
	    Path rutaDestino = Paths.get(rutaProductosString); // Ruta: /app/imagenes_subidas
	    
	    try {
	        // 🚨 PASO 1: ASEGURARSE DE QUE EL DIRECTORIO BASE EXISTA (NUEVA LÍNEA)
	        Files.createDirectories(rutaDestino); 

	        Files.copy(
	            foto.getInputStream(),
	            rutaDestino.resolve(nombreFotoProd), // Usar la ruta de destino
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

	@Override
	public boolean estaAsociadoAPedidos(Integer idProducto) {

	    return detalleRepo.countByIdIdprod(idProducto) > 0;
	}
}
