package tspw.proyuno.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

@Entity @Table(name="pedido")
public class Pedido {	
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Integer idpedido;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="idcliente")
	private Cliente idcliente;
	
	@CreationTimestamp  
	private LocalDateTime fecha;
	
	private double total;
	
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DetallePedido> detalles = new ArrayList<>();
	
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	@Fetch(FetchMode.JOIN)
	private List<Atender> atendidoPor = new ArrayList<>(); 
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idservicio", nullable = true)
    private Reserva reserva;
	
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
	
	public List<Atender> getAtendidoPor() {
		return atendidoPor;
	}
	public void setAtendidoPor(List<Atender> atendidoPor) {
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
		return "Pedido [idpedido=" + idpedido + ", idcliente=" + idcliente + ", fecha=" + fecha + ", total=" + total
				+ ", detalles=" + detalles + ", atendidoPor=" + atendidoPor + ", reserva=" + reserva + "]";
	}
	// import java.util.List;
	// Pedido.java
	public String getNombreDeQuienAtiende() {
	    if (atendidoPor == null || atendidoPor.isEmpty()) return "Sin asignar";
	    var primero = (atendidoPor instanceof java.util.List<?> l)
	            ? (tspw.proyuno.modelo.Atender) l.get(0)
	            : atendidoPor.stream().findFirst().orElse(null);
	    if (primero == null || primero.getEmpleado() == null) return "Sin asignar";
	    var emp = primero.getEmpleado();
	    var nombre = (emp.getNombreCompleto() != null && !emp.getNombreCompleto().isBlank())
	            ? emp.getNombreCompleto()
	            : emp.getNombreCompleto(); // por si tienes nombre simple
	    return (nombre == null || nombre.isBlank()) ? "Sin asignar" : nombre;
	}


}
