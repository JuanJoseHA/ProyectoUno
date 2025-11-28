package tspw.proyuno.config;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tspw.proyuno.modelo.Perfil;
import tspw.proyuno.modelo.Usuario;
import tspw.proyuno.repository.PerfilRepository;
import tspw.proyuno.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PerfilRepository perfilRepo;

    @Autowired
    private PasswordEncoder passwordEncoder; // usa el bean de DatabaseWebSecurity

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println(">>> DataInitializer corriendo...");

        if (perfilRepo.count() == 0) {
            Perfil admin = new Perfil(); admin.setNombre("Admin");
            Perfil cajero = new Perfil(); cajero.setNombre("Cajero");
            Perfil supervisor = new Perfil(); supervisor.setNombre("Supervisor");
            Perfil mesero = new Perfil(); mesero.setNombre("Mesero");
            Perfil cocinero = new Perfil(); cocinero.setNombre("Cocinero");
            Perfil cliente = new Perfil(); cliente.setNombre("Cliente");

            perfilRepo.saveAll(Arrays.asList(
                admin, cajero, supervisor, mesero, cocinero, cliente
            ));
        }

        if (usuarioRepo.findByUsername("admin").isEmpty()) {
            Perfil perfilAdmin = perfilRepo.findByNombre("Admin")
                    .orElseThrow(() -> new IllegalStateException("No se encontró el perfil 'Admin'"));

            Usuario admin = new Usuario();
            admin.setNombre("Administrador del Sistema");
            admin.setEmail("admin@restaurante.com");
            admin.setUsername("admin");
            admin.setEstatus(1);
            admin.setFechaRegistro(LocalDate.now());
            admin.setPassword(passwordEncoder.encode("admin")); // aquí usas tu encoder

            admin.getPerfiles().add(perfilAdmin);

            usuarioRepo.save(admin);
        }

        System.out.println(">>> DataInitializer terminado.");
    }
}
