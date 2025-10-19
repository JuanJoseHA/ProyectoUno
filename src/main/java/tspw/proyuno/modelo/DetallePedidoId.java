package tspw.proyuno.modelo;

import java.io.Serializable;
import jakarta.persistence.Embeddable;

@Embeddable
public class DetallePedidoId implements Serializable {

    private Integer idpedido;
    private Integer idprod;

    // 🔹 Constructor vacío (necesario para JPA)
    public DetallePedidoId() {
    }

    // 🔹 Constructor con ambos parámetros (aquí lo agregas)
    public DetallePedidoId(Integer idpedido, Integer idprod) {
        this.idpedido = idpedido;
        this.idprod = idprod;
    }

    // 🔹 Getters y Setters
    public Integer getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(Integer idpedido) {
        this.idpedido = idpedido;
    }

    public Integer getIdprod() {
        return idprod;
    }

    public void setIdprod(Integer idprod) {
        this.idprod = idprod;
    }

    // 🔹 equals() y hashCode() (importante para clave compuesta)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetallePedidoId that = (DetallePedidoId) o;
        return idpedido.equals(that.idpedido) && idprod.equals(that.idprod);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idpedido, idprod);
    }
}
