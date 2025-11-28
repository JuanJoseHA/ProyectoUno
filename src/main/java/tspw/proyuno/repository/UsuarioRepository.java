package tspw.proyuno.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tspw.proyuno.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Opcional: método de búsqueda para obtener un usuario por su username
	Optional<Usuario> findByUsername(String username);
}