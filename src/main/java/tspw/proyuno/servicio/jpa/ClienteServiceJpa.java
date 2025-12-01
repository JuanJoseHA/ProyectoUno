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

import tspw.proyuno.modelo.Cliente;
import tspw.proyuno.repository.ClienteRepository;
import tspw.proyuno.servicio.IClienteServicio;

@Service
@Primary
public class ClienteServiceJpa implements IClienteServicio {
	
	@Value("${PATH_PRODUCTO_IMAGENES:/app/imagenes_subidas}")
    private String rutaBaseString;
	
	@Autowired
	private ClienteRepository clienteRepo;

	@Override
	public List<Cliente> buscarTodosClientes() {
		// TODO Auto-generated method stub
		return clienteRepo.findAll();
	}

	@Override
	public Cliente buscarPorIdCliente(Integer idCliente) {
		
		Optional<Cliente> optional=clienteRepo.findById(idCliente);{
			if(optional.isPresent()) {
				return optional.get();
			}
		}
		return null;
	}

	@Override
	public void guardarCliente(Cliente cliente) {
		clienteRepo.save(cliente);
	}

	@Override
    public String guardarFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            return "noimagen.png";
        }
        
        Path rutaDestino = Paths.get(rutaBaseString, "cliente"); 

        String nombreArchivo = foto.getOriginalFilename();

        try {
            Files.createDirectories(rutaDestino); 

            Files.copy(
                foto.getInputStream(),
                rutaDestino.resolve(nombreArchivo),
                StandardCopyOption.REPLACE_EXISTING
            );
            return nombreArchivo;
        } catch (IOException e) {
            // Se lanza el error que viste en el log
            throw new RuntimeException("Error al guardar la foto: " + nombreArchivo, e); 
        }
    }

	@Override
	public void eliminarPorIdCliente(Integer idCliente) {
		// TODO Auto-generated method stub
		clienteRepo.deleteById(idCliente);
	}

	@Override
	public Cliente actualizarCliente(Integer idCliente, Cliente datos) {
        Cliente c = buscarPorIdCliente(idCliente); 
        c.setNombre(datos.getNombre());
        c.setApellidos(datos.getApellidos());
        c.setEmail(datos.getEmail());
        c.setCredito(datos.getCredito());
        c.setTelefono(datos.getTelefono());
        c.setDestacado(datos.getDestacado());
        if (datos.getFotoCliente() != null && !datos.getFotoCliente().isBlank()) {
            c.setFotoCliente(datos.getFotoCliente());
        }
        return clienteRepo.save(c);
	}

	@Override
	public List<Cliente> buscarPorNombre(String nombre) {
		// TODO Auto-generated method stub
		return clienteRepo.findByNombre(nombre);
	}

	@Override
	public List<Cliente> buscarPorNombreContiene(String texto) {
		// TODO Auto-generated method stub
		return clienteRepo.findByNombreContainingIgnoreCase(texto);
	}

	@Override
	public List<Cliente> buscarPorEmail(String email) {
		// TODO Auto-generated method stub
		return clienteRepo.findByEmail(email);
	}

	@Override
	public List<Cliente> buscarPorTerminaGmail(String gmail) {
		// TODO Auto-generated method stub
		return clienteRepo.findByEmailEndingWith(gmail);
	}

	@Override
	public List<Cliente> buscarEntreValores(Double min, Double max) {
		// TODO Auto-generated method stub
		return clienteRepo.findByCreditoBetween(min, max);
	}

	@Override
	public List<Cliente> buscarCreditoMayor(Double min) {
		// TODO Auto-generated method stub
		return clienteRepo.findByCreditoGreaterThanEqual(min);
	}

	@Override
	public List<Cliente> buscarPorDestacado() {
		// TODO Auto-generated method stub
		return clienteRepo.findByDestacado(1);
	}

	@Override
	public List<Cliente> buscarPorNombreyMayor(String nombre, Double min) {
		// TODO Auto-generated method stub
		return clienteRepo.findByNombreContainingIgnoreCaseAndCreditoGreaterThanEqual(nombre, min);
	}

	@Override
	public List<Cliente> buscarNoImagen(String fotoCliente) {
		// TODO Auto-generated method stub
		return clienteRepo.findByFotoCliente(fotoCliente);
	}

	@Override
	public List<Cliente> buscarCreditoDestacado(Double min) {
		// TODO Auto-generated method stub
		return clienteRepo.findByDestacadoAndCreditoGreaterThanEqual(1, min);
	}

	@Override
	public List<Cliente> buscar5Creditos() {
		// TODO Auto-generated method stub
		return clienteRepo.findTop5ByOrderByCreditoDesc();
	}

	@Override
    public Cliente buscarPorUsername(String username) {
        List<Cliente> clientes = clienteRepo.findByEmail(username); // Asumimos que el email es el username
        return clientes.isEmpty() ? null : clientes.get(0);
    }
	
	
	
	
	

}
