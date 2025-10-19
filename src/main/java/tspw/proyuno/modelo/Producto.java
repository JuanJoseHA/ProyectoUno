package tspw.proyuno.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="producto")

public class Producto {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idprod;
	
	private String nombreprod;
	private String descripcionprod;
	
	@Column(nullable = false)
	private double precio;
	 
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('Platillo','Bebida','Postre')")
	private TipoP tipo;
	
	private String fotoprod;

	
	public Integer getIdprod() {
		return idprod;
	}
	public void setIdprod(Integer idprod) {
		this.idprod = idprod;
	}

	
	public String getNombreprod() {
		return nombreprod;
	}
	public void setNombreprod(String nombreprod) {
		this.nombreprod = nombreprod;
	}

	
	public String getDescripcionprod() {
		return descripcionprod;
	}
	public void setDescripcionprod(String descripcionprod) {
		this.descripcionprod = descripcionprod;
	}

	
	public double getPrecio() {
		return precio;
	}
	public void setPrecio(double precio) {
		this.precio = precio;
	}

	
	public TipoP getTipo() {
		return tipo;
	}
	public void setTipo(TipoP tipo) {
		this.tipo = tipo;
	}

	
	public String getFotoprod() {
		return fotoprod;
	}
	public void setFotoprod(String fotoprod) {
		this.fotoprod = fotoprod;
	}
	@Override
	public String toString() {
		return "Producto [idprod=" + idprod + ", nombreprod=" + nombreprod + ", descripcionprod=" + descripcionprod
				+ ", precio=" + precio + ", tipo=" + tipo + ", fotoprod=" + fotoprod + "]";
	}

	public enum TipoP{
		Platillo,Bebida,Postre
	}
}


