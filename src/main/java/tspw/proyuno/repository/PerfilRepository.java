package tspw.proyuno.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tspw.proyuno.modelo.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, Integer> {

    Optional<Perfil> findByNombreIgnoreCase(String nombre);
    
    Optional<Perfil> findByNombre(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
