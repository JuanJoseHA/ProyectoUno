package tspw.proyuno.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

    /**
     * Define un PasswordEncoder que soporta múltiples formatos,
     * incluyendo el formato {noop} para contraseñas en texto plano.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usa este Factory para reconocer el prefijo {noop} de tu BD.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsManager usuarios(DataSource dataSource) {
        
    	JdbcUserDetailsManager users = new JdbcUserDetailsManager (dataSource);
    	
        // La columna "estatus" se mapea correctamente a la propiedad "enabled" de UserDetails
    	users.setUsersByUsernameQuery("select username,password,estatus from usuario u where username=?");
    	
        // Consulta para obtener los roles/autoridades del usuario
    	users.setAuthoritiesByUsernameQuery("select u.username,p.perfil from usuarioperfil up "
    	        + "inner join usuario u on u.id=up.idusuario "
    	        + "inner join perfil p on p.id=up.idperfil where u.username=?");
    	
    	return users;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/css/**", "/js/**", "/img/**", "/bootstrap/**", "/images/**", "/tinymce/**").permitAll()
                
                .requestMatchers("/", "/home", "/registro", "/cliente", "/busqueda/**", 
                                 "/listaProductos", "/verproducto/{idprod}").permitAll()
                
                .requestMatchers("/usuarios/**", "/perfiles/**").hasAuthority("Admin")

                .requestMatchers("/empleados/**", "/mesas/**").hasAnyAuthority("Admin")

                .requestMatchers("/listaProductosAdmin", "/verproductoadmin/{idprod}", 
                                 "/registroProductos", "/guardarProducto", 
                                 "/producto/modificar/{id}", "/producto/actualizar/{id}", 
                                 "/producto/eliminar/{id}").hasAnyAuthority("Admin")

                .requestMatchers("/lista", "/ver/{id}", "/modificar/{id}", 
                                 "/actualizar/{id}", "/eliminar/{id}").hasAnyAuthority("Admin")

                .requestMatchers("/pedidos/**", "/reservas/**").hasAnyAuthority("Admin","Mesero")

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