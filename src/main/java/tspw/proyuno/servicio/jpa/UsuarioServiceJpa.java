package tspw.proyuno.servicio.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tspw.proyuno.modelo.Empleado;
import tspw.proyuno.modelo.Perfil;
import tspw.proyuno.modelo.Usuario;
import tspw.proyuno.repository.UsuarioRepository;
import tspw.proyuno.servicio.IEmpleadoServicio;
import tspw.proyuno.servicio.IUsuarioServicio;

@Service
public class UsuarioServiceJpa implements IUsuarioServicio {

    @Autowired
    private UsuarioRepository usuarioRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IEmpleadoServicio empleadoService;

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
        }
    }

    @Override
    public void eliminar(Integer id) {
        usuarioRepo.deleteById(id);
    }

    @Override
    public Usuario buscarPorUsername(String username) {
        return usuarioRepo.findByUsername(username);
    }

    // ======================================================================
    //  LÓGICA PARA CREAR / ACTUALIZAR EMPLEADO A PARTIR DE LOS PERFILES
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
     * Solo se consideran Cocinero, Mesero y Cajero.
     * Supervisor NO es empleado en tu modelo actual (enum).
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
}
