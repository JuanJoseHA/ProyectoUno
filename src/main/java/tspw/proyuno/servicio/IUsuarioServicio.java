package tspw.proyuno.servicio;

import java.util.List;
import java.util.Optional;

import tspw.proyuno.modelo.Usuario;

public interface IUsuarioServicio {

    List<Usuario> listar();
    Usuario buscarPorId(Integer id);
    void guardar(Usuario usuario);
    void eliminar(Integer id);
    
    // Método para buscar por nombre de usuario (útil para login)
    Optional <Usuario> buscarPorUsername(String username);
}