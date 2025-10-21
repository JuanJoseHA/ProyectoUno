package tspw.proyuno.modelo;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity 
@Table(name = "atender")
public class Atender {
	
	@EmbeddedId
    private AtenderId id = new AtenderId();
	
	@ManyToOne(fetch = FetchType.EAGER)
    @MapsId("claveEmpleado")
    @JoinColumn(name = "clave_empleado", nullable = false)
    private Empleado empleado;

	@ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idpedido")
    @JoinColumn(name = "idpedido", nullable = false)
    private Pedido pedido;
	
	public Atender() {}
    
    public Atender(Empleado empleado, Pedido pedido) {
        this.empleado = empleado;
        this.pedido = pedido;
        this.id = new AtenderId(empleado.getClave(), pedido.getIdpedido());
    }

	public AtenderId getId() {
		return id;
	}

	public void setId(AtenderId id) {
		this.id = id;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	@Override
	public String toString() {
		return "Atender [empleado=" + empleado + ", pedido=" + pedido + "]";
	}
	
}
