package tspw.proyuno.servicio.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tspw.proyuno.modelo.Usuario;
import tspw.proyuno.repository.UsuarioRepository;
import tspw.proyuno.servicio.IUsuarioServicio;

@Service
public class UsuarioServiceJpa implements IUsuarioServicio {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    public List<Usuario> listar() {
        return usuarioRepo.findAll();
    }

    @Override
    public Usuario buscarPorId(Integer id) {
        return usuarioRepo.findById(id).orElse(null);
    }

    @Override
    public void guardar(Usuario usuario) {
        usuarioRepo.save(usuario);
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