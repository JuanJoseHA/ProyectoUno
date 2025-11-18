package tspw.proyuno.servicio.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tspw.proyuno.modelo.Usuario;
import tspw.proyuno.repository.UsuarioRepository;
import tspw.proyuno.servicio.IUsuarioServicio;

@Service
public class UsuarioServiceJpa implements IUsuarioServicio {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder; // Usa el delegating configurado en Security

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
                throw new IllegalArgumentException(
                    "La contraseña es obligatoria para un usuario nuevo."
                );
            }

            // Encriptamos usando el PasswordEncoder global (agrega {bcrypt} automáticamente)
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));

            // Guardamos directamente, incluyendo sus perfiles
            usuarioRepo.save(usuario);

        } else {
            // ===== CASO 2: ACTUALIZACIÓN =====

            Usuario existente = usuarioRepo.findById(usuario.getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualizar."));

            // Actualizar campos “normales”
            existente.setNombre(usuario.getNombre());
            existente.setUsername(usuario.getUsername());
            existente.setEmail(usuario.getEmail());
            existente.setEstatus(usuario.getEstatus());

            // Actualizar perfiles si vienen en el formulario
            if (usuario.getPerfiles() != null) {
                existente.setPerfiles(usuario.getPerfiles());
            }

            // Contraseña: solo si el admin/usuario escribió una nueva en el formulario
            if (nuevaPassword != null && !nuevaPassword.isBlank()) {
                existente.setPassword(passwordEncoder.encode(nuevaPassword));
            }
            // Si viene vacía, mantenemos la que ya está en BD.

            usuarioRepo.save(existente);
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
}
