package tspw.proyuno.controlador;

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

import tspw.proyuno.modelo.Producto;
import tspw.proyuno.servicio.IProductoServicio;

@Controller
public class ProductoControlador {
	
	@Autowired
	private IProductoServicio servicioProducto;
	
	@GetMapping("/listaProductos")
	public String mostrarProductos(Model model) {
		
		List<Producto> lista = servicioProducto.buscarProductos();
		model.addAttribute("ListaProductos", lista);
		
	    model.addAttribute("platillos", servicioProducto.buscarPorTipo(Producto.TipoP.Platillo));
	    model.addAttribute("bebidas", servicioProducto.buscarPorTipo(Producto.TipoP.Bebida));
	    model.addAttribute("postres", servicioProducto.buscarPorTipo(Producto.TipoP.Postre));
		
		return "producto/ListaProductos";
	}
	
	@GetMapping("/listaProductosAdmin")
	public String listarProductosAdmin(Model model) {
		
		// 1. Obtener la lista completa de productos
		List<Producto> productoLista = servicioProducto.buscarProductos();
		
		// 2. Pasar la lista al modelo
		model.addAttribute("productoLista", productoLista);
		
		// 3. Devolver la nueva plantilla de administración
		return "producto/productosListaAdmin"; 
	}
	
	@GetMapping("/verproducto/{idprod}")
	public String verDetallesProducto(@PathVariable("idprod") int idProducto, Model model) {
		Producto producto=servicioProducto.buscarProductoId(idProducto);
		model.addAttribute("producto", producto);
		return "producto/DetalleProducto";
		
	}
	
	@GetMapping("/registroProductos")
	public String mostrarFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("tipos", Producto.TipoP.values());
        return "producto/productoRegistro"; 
    }
	
	@PostMapping("/guardarProducto")
	public String guardarProducto(Producto producto, @RequestParam("foto") MultipartFile foto) {
		
		String nombreFoto = servicioProducto.guardarFoto(foto);
        producto.setFotoprod(nombreFoto);
		
		servicioProducto.guardarProducto(producto);
		
		System.out.println("Nuevo Producto: " + producto);
		
		return "redirect:/listaProductos";
	}
	
	@PostMapping("/producto/eliminar/{id}")
	public String eliminarProducto(@PathVariable("id") Integer idProducto) {
	    servicioProducto.eliminarPorIdProducto(idProducto);
	    return "redirect:/listaProductos";
	}

	@GetMapping("/producto/modificar/{id}")
	public String modificarProducto(@PathVariable("id") Integer idProducto, Model model) {
	    Producto producto = servicioProducto.buscarProductoId(idProducto);
	    if (producto == null) return "redirect:/listaProductos";

	    model.addAttribute("producto", producto);
	    model.addAttribute("tipos", Producto.TipoP.values());
	    model.addAttribute("titulo", "Modificar Producto");
	    return "producto/productoRegistro";
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
	    return "redirect:/listaProductos";
	}


}
