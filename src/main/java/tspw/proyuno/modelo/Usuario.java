package tspw.proyuno.modelo;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String email;
    private String password;
    private String username;
    private Integer estatus = 1; // 1=activo, 0=inactivo

    // Mejor usa LocalDate en JPA moderno
    private LocalDate fechaRegistro;
    
    

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) fechaRegistro = LocalDate.now();
        if (estatus == null) estatus = 1;
    }

    @ManyToMany(fetch = FetchType.LAZY) // <- sin cascade aquí (importante)
    @JoinTable(name = "usuarioperfil",
        joinColumns = @JoinColumn(name = "idusuario"),
        inverseJoinColumns = @JoinColumn(name = "idperfil"))
    
    private List<Perfil> perfiles;
    //private Set<Perfil> perfiles = new HashSet<>();

    // getters/setters
    

    public void agregarPerfil(Perfil p) { this.perfiles.add(p); }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getEstatus() {
		return estatus;
	}

	public void setEstatus(Integer estatus) {
		this.estatus = estatus;
	}

	public LocalDate getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	
	

	/*public Set<Perfil> getPerfiles() {
		return perfiles;
	}

	public void setPerfiles(Set<Perfil> perfiles) {
		this.perfiles = perfiles;
	}*/

	public List<Perfil> getPerfiles() {
		return perfiles;
	}

	public void setPerfiles(List<Perfil> perfiles) {
		this.perfiles = perfiles;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombre=" + nombre + ", email=" + email + ", password=" + password
				+ ", username=" + username + ", estatus=" + estatus + ", fechaRegistro=" + fechaRegistro + ", perfiles="
				+ perfiles + "]";
	}

	
}
