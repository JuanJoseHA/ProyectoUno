package tspw.proyuno.modelo;

import java.io.Serializable;

public class AtenderId implements Serializable {

	private String claveEmpleado;
    private Integer idpedido;
    
    public AtenderId() {}

    public AtenderId(String claveEmpleado, Integer idpedido) {
        this.claveEmpleado = claveEmpleado;
        this.idpedido = idpedido;
    }

    public String getClaveEmpleado() { return claveEmpleado; }
    public void setClaveEmpleado(String claveEmpleado) { this.claveEmpleado = claveEmpleado; }

    public Integer getIdpedido() { return idpedido; }
    public void setIdpedido(Integer idpedido) { this.idpedido = idpedido; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtenderId that = (AtenderId) o;
        return claveEmpleado.equals(that.claveEmpleado) && idpedido.equals(that.idpedido);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(claveEmpleado, idpedido);
    }
	
}
