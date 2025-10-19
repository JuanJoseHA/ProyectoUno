package tspw.proyuno.modelo;

public class Medicamento {
	
	private int id;
	private String codigo;
	private String nombre;
	private double precioCompra;
	private double precioVenta;
	private int stock;
	private String unidad;
	private String fotoMedicamento;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public double getPrecioCompra() {
		return precioCompra;
	}
	public void setPrecioCompra(double precioCompra) {
		this.precioCompra = precioCompra;
	}
	public double getPrecioVenta() {
		return precioVenta;
	}
	public void setPrecioVenta(double precioVenta) {
		this.precioVenta = precioVenta;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public String getUnidad() {
		return unidad;
	}
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}
	public String getFotoMedicamento() {
		return fotoMedicamento;
	}
	public void setFotoMedicamento(String fotoMedicamento) {
		this.fotoMedicamento = fotoMedicamento;
	}
	@Override
	public String toString() {
		return "Medicamento [id=" + id + ", codigo=" + codigo + ", nombre=" + nombre + ", precioCompra=" + precioCompra
				+ ", precioVenta=" + precioVenta + ", stock=" + stock + ", unidad=" + unidad + ", fotoMedicamento="
				+ fotoMedicamento + "]";
	}
	
	
	
	

}
