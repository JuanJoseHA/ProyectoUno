package tspw.proyuno.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name="Empleado")
public class Empleado {

	public enum Puesto { Cocinero, Mesero, Cajero }
	
	 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Integer idempleado;
	 
	 private String nombreCompleto;
	 
	 private String clave;
	 
	 @Enumerated(EnumType.STRING)
	 private Puesto puesto;

	 public Integer getIdempleado() {
		 return idempleado;
	 }

	 public void setIdempleado(Integer idempleado) {
		 this.idempleado = idempleado;
	 }

	 public String getNombreCompleto() {
		 return nombreCompleto;
	 }

	 public void setNombreCompleto(String nombreCompleto) {
		 this.nombreCompleto = nombreCompleto;
	 }

	 public String getClave() {
		 return clave;
	 }

	 public void setClave(String clave) {
		 this.clave = clave;
	 }

	 public Puesto getPuesto() {
		 return puesto;
	 }

	 public void setPuesto(Puesto puesto) {
		 this.puesto = puesto;
	 }

	 @Override
	 public String toString() {
		return "Empleado [idempleado=" + idempleado + ", nombreCompleto=" + nombreCompleto + ", clave=" + clave
				+ ", puesto=" + puesto + "]";
	 }
	 
	 
	
}
