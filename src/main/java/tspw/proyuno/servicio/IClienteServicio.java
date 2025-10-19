package tspw.proyuno.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import tspw.proyuno.modelo.Cliente;

public interface IClienteServicio {
	
	List<Cliente> buscarTodosClientes();
	
	Cliente buscarPorIdCliente (Integer idCliente);
	
	void guardarCliente (Cliente cliente);
	
	String guardarFoto(MultipartFile foto);
	
	void eliminarPorIdCliente(Integer idCliente);
	
	Cliente actualizarCliente(Integer idCliente,Cliente datos);

	Optional<Cliente> buscarPorNombre (String nombre);
	List<Cliente> buscarPorNombreContiene (String texto);
	Optional<Cliente> buscarPorEmail (String email);
	List<Cliente> buscarPorTerminaGmail (String gmail);
	List<Cliente> buscarEntreValores (Double min,Double max);
	List<Cliente> buscarCreditoMayor (Double min);
	List<Cliente> buscarPorDestacado ();
	List<Cliente> buscarPorNombreyMayor (String nombre,Double min);
	List<Cliente> buscarNoImagen (String fotoCliente);
	List<Cliente> buscarCreditoDestacado (Double min);
	List<Cliente> buscar5Creditos ();
	
}


