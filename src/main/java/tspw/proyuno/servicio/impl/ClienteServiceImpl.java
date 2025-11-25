package tspw.proyuno.servicio.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tspw.proyuno.modelo.Cliente;
import tspw.proyuno.servicio.IClienteServicio;


@Service
public class ClienteServiceImpl implements IClienteServicio {
	
	
	
	List<Cliente> listaCliente = null;

	Path rutaFotos = Paths.get("src/main/resources/static/imagenes/cliente");

	public ClienteServiceImpl() {
		
		listaCliente = new LinkedList<>();
		
		Cliente cliente1 = new Cliente();
		cliente1.setId(1);
		cliente1.setNombre("Juan");
		cliente1.setApellidos("Hernandez Atilano");
		cliente1.setEmail("juan_hernandez@gmail.com");
		cliente1.setCredito(10000);
		cliente1.setTelefono("7472234496");
		cliente1.setDestacado(1);
		cliente1.setFotoCliente("user1.png");
		
		Cliente cliente2 = new Cliente();
		cliente2.setId(2);
		cliente2.setNombre("Jose");
		cliente2.setApellidos("Morales Corona");
		cliente2.setEmail("jose_morales@gmail.com");
		cliente2.setCredito(7800);
		cliente2.setTelefono("7472458614");
		cliente2.setDestacado(0);
		cliente2.setFotoCliente("user2.png");
		
		Cliente cliente3 = new Cliente();
		cliente3.setId(3);
		cliente3.setNombre("Mariana");
		cliente3.setApellidos("Rivera Salmeron");
		cliente3.setEmail("mari_rivera@gmail.com");
		cliente3.setCredito(12600);
		cliente3.setTelefono("7472657893");
		cliente3.setDestacado(1);
		cliente3.setFotoCliente("foto1.jpg");
		
		listaCliente.add(cliente1);
		listaCliente.add(cliente2);
		listaCliente.add(cliente3);
		
	}
	
	@Override
	public List<Cliente> buscarTodosClientes() {
		// TODO Auto-generated method stub
		return listaCliente;
	}
	
	public Cliente buscarPorIdCliente (Integer idCliente) {
		for(Cliente cli:listaCliente)
			if(cli.getId() == idCliente)
				return cli;
		return null;
		
	}
	
	public void guardarCliente (Cliente cliente) {
		listaCliente.add(cliente);
	}

	@Override
	public String guardarFoto(MultipartFile foto) {
	    if (foto == null || foto.isEmpty()) {
	        return null;
	    }

	    String nombreArchivo = foto.getOriginalFilename();

	    try {
	        Files.copy(
	            foto.getInputStream(),
	            rutaFotos.resolve(nombreArchivo),
	            StandardCopyOption.REPLACE_EXISTING
	        );
	        return nombreArchivo;
	    } catch (IOException e) {
	        throw new RuntimeException("Error al guardar la foto: " + nombreArchivo, e);
	    }
	}

	@Override
	public void eliminarPorIdCliente(Integer idCliente) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Cliente actualizarCliente(Integer idCliente, Cliente datos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cliente> buscarPorNombreContiene(String texto) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Cliente> buscarPorTerminaGmail(String gmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cliente> buscarEntreValores(Double min, Double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cliente> buscarCreditoMayor(Double min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cliente> buscarPorDestacado() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cliente> buscarPorNombreyMayor(String nombre, Double min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cliente> buscarNoImagen(String fotoCliente) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cliente> buscarCreditoDestacado(Double min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cliente> buscar5Creditos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cliente> buscarPorNombre(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cliente> buscarPorEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cliente buscarPorUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
