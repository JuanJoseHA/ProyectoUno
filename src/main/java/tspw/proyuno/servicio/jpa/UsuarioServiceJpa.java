package tspw.proyuno.servicio.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tspw.proyuno.modelo.Cliente;
import tspw.proyuno.modelo.Empleado;
import tspw.proyuno.modelo.Perfil;
import tspw.proyuno.modelo.Usuario;
import tspw.proyuno.repository.UsuarioRepository;
import tspw.proyuno.servicio.IClienteServicio;
import tspw.proyuno.servicio.IEmpleadoServicio;
import tspw.proyuno.servicio.IPedidoServicio; 
import tspw.proyuno.servicio.IReservaServicio; 
import tspw.proyuno.servicio.IUsuarioServicio;

@Service
public class UsuarioServiceJpa implements IUsuarioServicio {

    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IEmpleadoServicio empleadoService;
    @Autowired
    private IClienteServicio clienteService;
    @Autowired
    private IPedidoServicio pedidoService;
    @Autowired
    private IReservaServicio reservaService;

    @Override
    public List<Usuario> listar() {
        return usuarioRepo.findAll();
    }

    @Override
    public Usuario buscarPorId(Integer id) {
        return usuarioRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void guardar(Usuario usuario) {

        String nuevaPassword = usuario.getPassword();

        if (usuario.getId() == null) {
            // ===== CASO 1: NUEVO USUARIO =====
            if (nuevaPassword == null || nuevaPassword.isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para un usuario nuevo.");
            }

            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            Usuario guardado = usuarioRepo.save(usuario);

            // Crear/actualizar empleado en base a sus perfiles
            crearOActualizarEmpleadoSiEsTrabajador(guardado);

        } else {
            // ===== CASO 2: ACTUALIZACIÓN =====
            Usuario existente = usuarioRepo.findById(usuario.getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualizar."));

            // Actualizar campos simples
            existente.setNombre(usuario.getNombre());
            existente.setUsername(usuario.getUsername());
            existente.setEmail(usuario.getEmail());
            existente.setEstatus(usuario.getEstatus());

            // Actualizar perfiles (roles)
            if (usuario.getPerfiles() != null) {
                existente.setPerfiles(usuario.getPerfiles());
            }

            // Contraseña: solo si se envía una nueva
            if (nuevaPassword != null && !nuevaPassword.isBlank()) {
                existente.setPassword(passwordEncoder.encode(nuevaPassword));
            }

            Usuario guardado = usuarioRepo.save(existente);

            // Crear/actualizar empleado en base a sus perfiles
            crearOActualizarEmpleadoSiEsTrabajador(guardado);
            
            eliminarClienteSiAplica(guardado); 
        }
    }

    @Override
    public void eliminar(Integer id) {
        usuarioRepo.deleteById(id);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepo.findByUsername(username);
    }

    // ======================================================================
    // LÓGICA PARA CREAR / ACTUALIZAR EMPLEADO
    // ======================================================================

    /**
     * Si el usuario tiene perfil Cocinero/Mesero/Cajero, se asegura que exista
     * un registro en Empleado con clave = username y puesto correcto.
     */
    private void crearOActualizarEmpleadoSiEsTrabajador(Usuario usuario) {
        if (usuario.getPerfiles() == null || usuario.getPerfiles().isEmpty()) {
            return;
        }

        // Determinar el puesto en base a los perfiles
        Empleado.Puesto puesto = obtenerPuestoDesdePerfiles(usuario);
        if (puesto == null) {
            // No es Cocinero/Mesero/Cajero → no lo tratamos como empleado
            return;
        }

        // Buscar empleado por username (ligado por clave)
        String clave = usuario.getUsername();
        if (clave == null || clave.isBlank()) {
            return;
        }

        Empleado emp = empleadoService.buscarPorUsername(clave);

        if (emp == null) {
            // Crear nuevo empleado
            emp = new Empleado();
            emp.setClave(clave);
        }

        // Actualizar datos básicos
        emp.setNombreCompleto(usuario.getNombre());
        emp.setPuesto(puesto);

        empleadoService.guardar(emp);
    }

    /**
     * Mapea los perfiles del usuario a un Puesto de Empleado.
     */
    private Empleado.Puesto obtenerPuestoDesdePerfiles(Usuario usuario) {
        for (Perfil p : usuario.getPerfiles()) {
            if (p == null || p.getNombre() == null) continue;
            String rol = p.getNombre().trim();

            if (rol.equalsIgnoreCase("Mesero")) {
                return Empleado.Puesto.Mesero;
            } else if (rol.equalsIgnoreCase("Cocinero")) {
                return Empleado.Puesto.Cocinero;
            } else if (rol.equalsIgnoreCase("Cajero")) {
                return Empleado.Puesto.Cajero;
            }
        }
        return null; // Supervisor/Admin/Cliente → no se consideran Empleado
    }
    

 // En UsuarioServiceJpa.java

    private void eliminarClienteSiAplica(Usuario usuario) {
        
        // 1. Verificar si el usuario AHORA NO tiene el perfil "Cliente" en su lista de perfiles
        boolean tienePerfilCliente = usuario.getPerfiles().stream()
            .anyMatch(p -> p.getNombre().equalsIgnoreCase("Cliente"));
            
        // Si el usuario todavía tiene el perfil "Cliente", no se elimina el registro de Cliente.
        // Solo procedemos si ya no es un cliente.
        if (tienePerfilCliente) {
            return; 
        }
        
        // 🚨 A PARTIR DE AQUÍ: El usuario ya no es cliente (fue 'promovido'). Procede con la eliminación.

        // 2. Buscar al Cliente asociado (la asociación es por email en este sistema)
        List<Cliente> clientes = clienteService.buscarPorEmail(usuario.getEmail());
        if (clientes == null || clientes.isEmpty()) {
            // No existe registro de Cliente asociado.
            return; 
        }
        Cliente cliente = clientes.get(0);
        Integer idCliente = cliente.getId();

        // 3. Comprobar si el Cliente tiene Pedidos o Reservas activas
        boolean tienePedidos = pedidoService.contarPedidosPorClienteId(idCliente) > 0;
        boolean tieneReservas = !reservaService.buscarReservasPorClienteId(idCliente).isEmpty();

        if (tienePedidos || tieneReservas) {
            // Si tiene dependencias, NO se elimina, y se registra la advertencia.
            System.out.println("ADVERTENCIA: Cliente " + idCliente + " tiene dependencias (Pedidos/Reservas) y no se elimina.");
            return;
        }

        // 4. Si no tiene dependencias y ya no tiene el rol de Cliente, eliminar el Cliente
        try {
            clienteService.eliminarPorIdCliente(idCliente);
            System.out.println("Cliente " + idCliente + " eliminado exitosamente ya que fue promovido a un nuevo rol y no tenía dependencias.");
        } catch (Exception e) {
            System.err.println("Error al intentar eliminar el Cliente " + idCliente + ": " + e.getMessage());
        }
    }
}