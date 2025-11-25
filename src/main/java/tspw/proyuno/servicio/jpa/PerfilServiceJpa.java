package tspw.proyuno.servicio.jpa;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tspw.proyuno.modelo.Perfil;
import tspw.proyuno.repository.PerfilRepository;
import tspw.proyuno.servicio.IPerfilServicio;

@Service("perfilServiceJpa")
public class PerfilServiceJpa implements IPerfilServicio {

    private final PerfilRepository repo;

    public PerfilServiceJpa(PerfilRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Perfil> listar() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Perfil buscarPorId(Integer id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Perfil guardar(Perfil perfil) {
        return repo.save(perfil);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeNombre(String nombre) {
        if (nombre == null) return false;
        return repo.existsByNombreIgnoreCase(nombre.trim());
    }
    
    // NUEVA IMPLEMENTACIÓN: Buscar perfil por nombre ignorando mayúsculas/minúsculas
    @Override
    @Transactional(readOnly = true)
    public Perfil buscarPorNombre(String nombre) {
        if (nombre == null) return null;
        return repo.findByNombreIgnoreCase(nombre.trim()).orElse(null);
    }
}