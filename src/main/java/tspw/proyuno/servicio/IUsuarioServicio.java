package tspw.proyuno.servicio;

import java.util.List;

import tspw.proyuno.modelo.Usuario;

public interface IUsuarioServicio {

    List<Usuario> listar();
    Usuario buscarPorId(Integer id);
    void guardar(Usuario usuario);
    void eliminar(Integer id);
    
    // Método para buscar por nombre de usuario (útil para login)
    Usuario buscarPorUsername(String username);
}