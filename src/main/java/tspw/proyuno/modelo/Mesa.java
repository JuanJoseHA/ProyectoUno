package tspw.proyuno.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name="Mesa")
public class Mesa {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idmesa;
	
	private String ubicacion;
	
	private Integer capacidad;

	public Integer getIdmesa() {
		return idmesa;
	}

	public void setIdmesa(Integer idmesa) {
		this.idmesa = idmesa;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public Integer getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(Integer capacidad) {
		this.capacidad = capacidad;
	}

	@Override
	public String toString() {
		return "Mesa [idmesa=" + idmesa + ", ubicacion=" + ubicacion + ", capacidad=" + capacidad + "]";
	}
	
	

}
