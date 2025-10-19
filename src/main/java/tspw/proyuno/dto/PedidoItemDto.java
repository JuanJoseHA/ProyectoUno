package tspw.proyuno.dto;

// Clase auxiliar para transferir producto + cantidad del formulario del carrito
public class PedidoItemDto {

    private Integer productoId;
    private Integer cantidad;

    // Constructor vacío (necesario para Spring)
    public PedidoItemDto() {
    }

    // Constructor con parámetros
    public PedidoItemDto(Integer productoId, Integer cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
