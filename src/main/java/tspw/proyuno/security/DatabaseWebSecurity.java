package tspw.proyuno.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Soporta {bcrypt}, {noop}, etc.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsManager usuarios(DataSource dataSource) {

        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);

        users.setUsersByUsernameQuery(
                "select username,password,estatus from usuario u where username=?");

        users.setAuthoritiesByUsernameQuery(
                "select u.username,p.perfil from usuarioperfil up " +
                "inner join usuario u on u.id=up.idusuario " +
                "inner join perfil p on p.id=up.idperfil where u.username=?");

        return users;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // ===================== PÚBLICO / SIN LOGIN =====================
            		.requestMatchers("/css/**", "/js/**", "/img/**",
                            "/bootstrap/**", "/images/**", "/imagenes/**", "/tinymce/**")
            		.permitAll()


                // Página principal y navegación pública
                .requestMatchers("/", "/home",
                                 "/busqueda/**", "/cliente",
                                 "/listaProductos", "/verproducto/**")
                    .permitAll()

                // Registro PÚBLICO de clientes (form y post)
                .requestMatchers("/registro", "/guardar")
                    .permitAll()

                // ===================== CLIENTES =====================
                // Cajero + Admin: ver lista y detalle de clientes
                .requestMatchers("/lista", "/ver/{id}")
                    .hasAnyAuthority("Admin", "Cajero")

                // Admin: modificar / actualizar / eliminar clientes
                .requestMatchers("/modificar/{id}",
                                 "/actualizar/{id}",
                                 "/eliminar/{id}")
                    .hasAuthority("Admin")

                // ===================== MESAS / PRODUCTOS / EMPLEADOS =====================
                // Supervisor + Admin: CRUD de mesas, productos admin y empleados
                .requestMatchers("/mesas/**", "/empleados/**")
                    .hasAnyAuthority("Admin", "Supervisor")

                .requestMatchers("/listaProductosAdmin",
                                 "/verproductoadmin/**",
                                 "/registroProductos",
                                 "/guardarProducto",
                                 "/producto/modificar/**",
                                 "/producto/actualizar/**",
                                 "/producto/eliminar/**")
                    .hasAnyAuthority("Admin", "Supervisor")

                // ===================== PEDIDOS =====================
                // Ver lista y detalle de pedidos (Admin, Cajero, Mesero, Cocinero)
                .requestMatchers(HttpMethod.GET,
                                 "/pedidos",
                                 "/pedidos/{id}",
                                 "/pedidos/{id}/pdf")
                    .hasAnyAuthority("Admin", "Cajero", "Mesero", "Cocinero")

                // Crear pedidos (lista + formulario + guardar) - Admin y Cajero
                .requestMatchers("/pedidos/nuevo",
                                 "/pedidos/guardar")
                    .hasAnyAuthority("Admin", "Cajero")

                // Editar pedidos (solo Admin, Cajero y Mesero)
                .requestMatchers("/pedidos/editar/**",
                                 "/pedidos/actualizar/**")
                    .hasAnyAuthority("Admin", "Cajero", "Mesero")

                // Eliminar pedidos (solo Admin)
                .requestMatchers("/pedidos/eliminar/**")
                    .hasAuthority("Admin")

                // ===================== RESERVAS =====================
                // Crear reservas (Admin, Cajero, Cliente)
                .requestMatchers("/reservas/nuevo",
                                 "/reservas/guardar")
                    .hasAnyAuthority("Admin", "Cajero", "Cliente")

                // Ver/gestionar reservas (Admin, Cajero, Cliente)
                // El filtro "ver solo las suyas" se hace en el controlador
                .requestMatchers("/reservas/**")
                    .hasAnyAuthority("Admin", "Cajero", "Cliente")

                // ===================== USUARIOS / PERFILES =====================
                // Solo Admin
                .requestMatchers("/usuarios/**", "/perfiles/**")
                    .hasAuthority("Admin")

                // ===================== CUALQUIER OTRO ENDPOINT =====================
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(Customizer.withDefaults())
            .csrf(Customizer.withDefaults());

        return http.build();
    }
}
