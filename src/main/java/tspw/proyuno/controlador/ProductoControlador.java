package tspw.proyuno.controlador;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tspw.proyuno.modelo.Producto;
import tspw.proyuno.modelo.Producto.TipoP;
import tspw.proyuno.servicio.IProductoServicio;

@Controller
public class ProductoControlador {
	
	@Autowired
	private IProductoServicio servicioProducto;
	
	@GetMapping("/listaProductos")
	public String mostrarProductos(Model model,
            @RequestParam(value = "min", required = false) Double min,
            @RequestParam(value = "max", required = false) Double max) {

		List<Producto> productosFiltrados;
		
		if (min != null && max != null) {
		productosFiltrados = servicioProducto.buscarPorPrecioEntre(min, max);
		model.addAttribute("busquedaPrecioActiva", true);
		} else {
		productosFiltrados = servicioProducto.buscarProductos();
		model.addAttribute("busquedaPrecioActiva", false);
		}
		
		List<Producto> platillos = new ArrayList<>();
		List<Producto> bebidas = new ArrayList<>();
		List<Producto> postres = new ArrayList<>();
		
		for (Producto p : productosFiltrados) {
		if (p.getTipo() == TipoP.Platillo) {
		platillos.add(p);
		} else if (p.getTipo() == TipoP.Bebida) {
		bebidas.add(p);
		} else if (p.getTipo() == TipoP.Postre) {
		postres.add(p);
		}
		}

		model.addAttribute("platillos", platillos);
		model.addAttribute("bebidas", bebidas);
		model.addAttribute("postres", postres);
		
		return "Producto/listaProductos";
	}
	
	@GetMapping("/listaProductosAdmin")
	public String listarProductosAdmin(Model model,
                                       @RequestParam(value = "nombre", required = false) String nombre,
                                       @RequestParam(value = "tipo", required = false) TipoP tipo) {
		
		List<Producto> productoLista;

        if ((nombre != null && !nombre.isBlank()) || tipo != null) {
            productoLista = servicioProducto.buscarPorNombreYTipo(nombre, tipo);
            model.addAttribute("busquedaActiva", true);
        } else {
            productoLista = servicioProducto.buscarProductos();
            model.addAttribute("busquedaActiva", false);
        }

		model.addAttribute("productoLista", productoLista);
        model.addAttribute("tipos", Producto.TipoP.values()); 
		
		return "Producto/productosListaAdmin"; 
	}
	
	@GetMapping("/verproducto/{idprod}")
	public String verDetallesProducto(@PathVariable("idprod") int idProducto, Model model) {
		Producto producto=servicioProducto.buscarProductoId(idProducto);
		model.addAttribute("producto", producto);
		return "Producto/DetalleProducto";
		
	}
	
	@GetMapping("/verproductoadmin/{idprod}")
	public String verDetallesProductoadmin(@PathVariable("idprod") int idProducto, Model model) {
		Producto producto=servicioProducto.buscarProductoId(idProducto);
		model.addAttribute("producto", producto);
		return "Producto/detalleProductoAdmin";
		
	}
	
	@GetMapping("/registroProductos")
	public String mostrarFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("tipos", Producto.TipoP.values());
        return "Producto/productoRegistro"; 
    }
	
	@PostMapping("/guardarProducto")
	public String guardarProducto(Producto producto, @RequestParam(value = "foto", required = false) MultipartFile foto) {
		
		String nombreFoto = servicioProducto.guardarFoto(foto);
        producto.setFotoprod(nombreFoto);
		
		servicioProducto.guardarProducto(producto);
		
		System.out.println("Nuevo Producto: " + producto);
		
		return "redirect:/listaProductosAdmin";
	}
	
	@PostMapping("/producto/eliminar/{id}")
	public String eliminarProducto(@PathVariable("id") Integer idProducto, RedirectAttributes flash) {
	    
		if (servicioProducto.estaAsociadoAPedidos(idProducto)) {
	        Producto p = servicioProducto.buscarProductoId(idProducto);
	        flash.addFlashAttribute("error", 
	            "El producto ´" + p.getNombreprod() + "´ no puede ser eliminado porque está asociado a uno o más pedidos.");
	    } else {
	        servicioProducto.eliminarPorIdProducto(idProducto);
	        flash.addFlashAttribute("msg", "Producto eliminado correctamente.");
	    }
		
	    return "redirect:/listaProductosAdmin";
	}

	@GetMapping("/producto/modificar/{id}")
	public String modificarProducto(@PathVariable("id") Integer idProducto, Model model) {
	    Producto producto = servicioProducto.buscarProductoId(idProducto);
	    if (producto == null) return "redirect:/listaProductos";

	    model.addAttribute("producto", producto);
	    model.addAttribute("tipos", Producto.TipoP.values());
	    model.addAttribute("titulo", "Modificar Producto");
	    return "Producto/productoRegistro";
	}

	@PostMapping("/producto/actualizar/{id}")
	public String actualizarProducto(@PathVariable("id") Integer idProducto,
	                                 @ModelAttribute("producto") Producto datos,
	                                 @RequestParam(value = "foto", required = false) MultipartFile foto) {
	    if (foto != null && !foto.isEmpty()) {
	        String nombreFoto = servicioProducto.guardarFoto(foto);
	        datos.setFotoprod(nombreFoto);
	    }
	    servicioProducto.actualizarProducto(idProducto, datos);
	    return "redirect:/listaProductosAdmin";
	}


}
