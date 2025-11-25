package tspw.proyuno.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="pedido")
public class Pedido {	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idpedido;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idcliente")
	private Cliente idcliente;
	
	@CreationTimestamp  
	private LocalDateTime fecha;
	
	private double total;
	
	// Colección de detalles: List está bien, pero mejor LAZY
	@OneToMany(mappedBy = "pedido",
	           cascade = CascadeType.ALL,
	           orphanRemoval = true,
	           fetch = FetchType.LAZY)
	private List<DetallePedido> detalles = new ArrayList<>();
	
	// Colección de "Atender": la pasamos a Set para evitar MultipleBagFetchException
	@OneToMany(mappedBy = "pedido",
	           cascade = CascadeType.ALL,
	           orphanRemoval = true,
	           fetch = FetchType.LAZY)
	private Set<Atender> atendidoPor = new HashSet<>(); 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idservicio", nullable = true)
	private Reserva reserva;
	
	// ===== Getters y Setters =====
	
	public Integer getIdpedido() {
		return idpedido;
	}
	public void setIdpedido(Integer idpedido) {
		this.idpedido = idpedido;
	}
	
	public Cliente getIdcliente() {
		return idcliente;
	}
	public void setIdcliente(Cliente idcliente) {
		this.idcliente = idcliente;
	}
	
	public LocalDateTime getFecha() {
		return fecha;
	}
	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
	
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}

	public List<DetallePedido> getDetalles() {
		return detalles;
	}
	public void setDetalles(List<DetallePedido> detalles) {
		this.detalles = detalles;
	}
	
	public Set<Atender> getAtendidoPor() {
		return atendidoPor;
	}
	public void setAtendidoPor(Set<Atender> atendidoPor) {
		this.atendidoPor = atendidoPor;
	}
	
	public Reserva getReserva() {
		return reserva;
	}
	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}
	
	@Override
	public String toString() {
		return "Pedido [idpedido=" + idpedido +
		       ", idcliente=" + idcliente +
		       ", fecha=" + fecha +
		       ", total=" + total +
		       ", detalles=" + detalles +
		       ", atendidoPor=" + atendidoPor +
		       ", reserva=" + reserva + "]";
	}

	// Devuelve el nombre de la primera persona que atiende el pedido
	public String getNombreDeQuienAtiende() {
	    if (atendidoPor == null || atendidoPor.isEmpty()) {
	        return "Sin asignar";
	    }
	    
	    Atender primero = atendidoPor.iterator().next();
	    if (primero == null || primero.getEmpleado() == null) {
	        return "Sin asignar";
	    }
	    
	    var emp = primero.getEmpleado();
	    String nombre = emp.getNombreCompleto();
	    return (nombre == null || nombre.isBlank()) ? "Sin asignar" : nombre;
	}
}
