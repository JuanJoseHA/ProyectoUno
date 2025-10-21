package tspw.proyuno.modelo;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity @Table(name="Empleado")
public class Empleado {

	public enum Puesto { Cocinero, Mesero, Cajero }
	
	 @Id 
	 private String clave;
	 
	 private String nombreCompleto;
	 
	 @Enumerated(EnumType.STRING)
	 private Puesto puesto;
	 
	 @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
	 private List<Atender> pedidosAtendidos;

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
		return "Empleado [clave=" + clave + ", nombreCompleto=" + nombreCompleto + ", puesto=" + puesto + "]";
	 }

	 
	 
	
}
