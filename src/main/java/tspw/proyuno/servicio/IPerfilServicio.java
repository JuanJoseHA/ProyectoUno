package tspw.proyuno.servicio;

import java.util.List;

import tspw.proyuno.modelo.Perfil;

public interface IPerfilServicio {

    List<Perfil> listar();

    Perfil buscarPorId(Integer id);

    Perfil guardar(Perfil perfil);

    void eliminar(Integer id);

    boolean existeNombre(String nombre);
    
    // NUEVA FUNCIÓN: Buscar perfil por nombre
    Perfil buscarPorNombre(String nombre);
}