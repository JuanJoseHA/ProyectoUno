package tspw.proyuno.servicio.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tspw.proyuno.modelo.Producto;
import tspw.proyuno.servicio.IProductoServicio;

@Service
public class ProductoServiceImp implements IProductoServicio {
	
    private final List<Producto> listaProductos;
    private final Path rutaProductos = Paths.get("C:/Producto");

    public ProductoServiceImp() {
        listaProductos = new LinkedList<>();

        Producto p1 = new Producto();
        p1.setIdprod(1);
        p1.setNombreprod("Enchiladas Verdes");
        p1.setDescripcionprod("Tortillas rellenas de pollo bañadas en salsa verde con queso y crema");
        p1.setPrecio(85.00);
        p1.setTipo(Producto.TipoP.Platillo);
        p1.setFotoprod("enchiladas.jpg");
        listaProductos.add(p1);

        Producto p2 = new Producto();
        p2.setIdprod(2);
        p2.setNombreprod("Tacos al Pastor");
        p2.setDescripcionprod("Tortillas de maíz con carne al pastor, piña y salsa");
        p2.setPrecio(60.00);
        p2.setTipo(Producto.TipoP.Platillo);
        p2.setFotoprod("tacos.jpg");
        listaProductos.add(p2);

        Producto p3 = new Producto();
        p3.setIdprod(3);
        p3.setNombreprod("Pozole Rojo");
        p3.setDescripcionprod("Tradicional pozole con carne de cerdo y maíz cacahuazintle");
        p3.setPrecio(90.00);
        p3.setTipo(Producto.TipoP.Platillo);
        p3.setFotoprod("pozole.jpg");
        listaProductos.add(p3);
        
        Producto p10 = new Producto();
        p10.setIdprod(10);
        p10.setNombreprod("Mole Poblano");
        p10.setDescripcionprod("Pollo bañado en mole tradicional poblano con ajonjolí");
        p10.setPrecio(120.00);
        p10.setTipo(Producto.TipoP.Platillo);
        p10.setFotoprod("mole.jpg");
        listaProductos.add(p10);

        Producto p4 = new Producto();
        p4.setIdprod(4);
        p4.setNombreprod("Agua de Horchata");
        p4.setDescripcionprod("Refrescante bebida de arroz con canela");
        p4.setPrecio(25.00);
        p4.setTipo(Producto.TipoP.Bebida);
        p4.setFotoprod("horchata.jpg");
        listaProductos.add(p4);

        Producto p5 = new Producto();
        p5.setIdprod(5);
        p5.setNombreprod("Agua de Jamaica");
        p5.setDescripcionprod("Agua fresca hecha con flor de jamaica natural");
        p5.setPrecio(20.00);
        p5.setTipo(Producto.TipoP.Bebida);
        p5.setFotoprod("jamaica.png");
        listaProductos.add(p5);

        Producto p6 = new Producto();
        p6.setIdprod(6);
        p6.setNombreprod("Café de Olla");
        p6.setDescripcionprod("Café preparado con canela y piloncillo");
        p6.setPrecio(30.00);
        p6.setTipo(Producto.TipoP.Bebida);
        p6.setFotoprod("cafe.jpg");
        listaProductos.add(p6);

        Producto p7 = new Producto();
        p7.setIdprod(7);
        p7.setNombreprod("Pastel");
        p7.setDescripcionprod("Delicioso pastel esponjoso con mezcla de tres leches");
        p7.setPrecio(55.00);
        p7.setTipo(Producto.TipoP.Postre);
        p7.setFotoprod("pastel.jpg");
        listaProductos.add(p7);

        Producto p8 = new Producto();
        p8.setIdprod(8);
        p8.setNombreprod("Flan Napolitano");
        p8.setDescripcionprod("Flan casero con caramelo");
        p8.setPrecio(40.00);
        p8.setTipo(Producto.TipoP.Postre);
        p8.setFotoprod("flan.jpg");
        listaProductos.add(p8);

        Producto p9 = new Producto();
        p9.setIdprod(9);
        p9.setNombreprod("Arroz con Leche");
        p9.setDescripcionprod("Postre típico con canela y leche condensada");
        p9.setPrecio(35.00);
        p9.setTipo(Producto.TipoP.Postre);
        p9.setFotoprod("arrozconleche.jpg");
        listaProductos.add(p9);
    }

    @Override
    public List<Producto> buscarProductos() {
        return listaProductos;
    }

    @Override
    public Producto buscarProductoId(Integer idProd) {
        for (Producto pro : listaProductos) {
            if (pro.getIdprod().equals(idProd)) {
                return pro;
            }
        }
        return null;
    }

    @Override
    public List<Producto> buscarPorTipo(Producto.TipoP tipo) {
        return listaProductos.stream()
                .filter(p -> p.getTipo() == tipo)
                .toList();
    }

	@Override
	public void guardarProducto(Producto producto) {
		listaProductos.add(producto);
	}

	@Override
	public String guardarFoto(MultipartFile foto) {

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public Producto actualizarProducto(Integer idProducto, Producto datos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean estaAsociadoAPedidos(Integer idProducto) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
    
    
}

