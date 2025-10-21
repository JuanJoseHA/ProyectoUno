package tspw.proyuno.modelo;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity @Table(name="detalle_pedido")
public class DetallePedido {

	@EmbeddedId
	private DetallePedidoId id = new DetallePedidoId();
	
	 @ManyToOne(fetch = FetchType.LAZY)
	  @MapsId("idpedido")
	  @JoinColumn(name = "idpedido", nullable = false)
	  private Pedido pedido;
	 
	 @ManyToOne(fetch = FetchType.LAZY)
	  @MapsId("idprod")
	  @JoinColumn(name = "idprod", nullable = false)
	  private Producto producto;
	
	 private Integer cantidad;
	 
	 
	 
	 public Producto getProducto() {
		return producto;
	}
	 public void setProducto(Producto producto) {
		 this.producto = producto;
	 }
	 public Pedido getPedido() {
		return pedido;
	}
	 public void setPedido(Pedido pedido) {
		 this.pedido = pedido;
	 }
	 public DetallePedidoId getId() {
		 return id;
	 }
	 public void setId(DetallePedidoId id) {
		 this.id = id;
	 }
	 public Integer getCantidad() {
		 return cantidad;
	 }
	 public void setCantidad(Integer cantidad) {
		 this.cantidad = cantidad;
	 }
	 @Override
	 public String toString() {
		return "DetallePedido [id=" + id + ", pedido=" + pedido + ", producto=" + producto + ", cantidad=" + cantidad
				+ "]";
	 }

	 
	 

}
