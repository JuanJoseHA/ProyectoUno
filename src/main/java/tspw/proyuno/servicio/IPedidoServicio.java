package tspw.proyuno.servicio;

import java.util.List;

import tspw.proyuno.dto.PedidoItemDto;
import tspw.proyuno.modelo.Pedido;

public interface IPedidoServicio {

	List<Pedido> mostrarPedidos();
	  Pedido buscarPorId(Integer idPedido);
	  Pedido crearPedido(Integer idCliente, String claveMesero, Integer idReserva, List<PedidoItemDto> items);             
	  void eliminarPedido(Integer idPedido);
	  Pedido actualizarPedido(Integer idPedido, Integer idCliente, String claveMesero, Integer idReserva, List<PedidoItemDto> items);
	  
	  // NUEVAS FUNCIONES PARA AUTORIZACIÓN DE GRANULARIDAD FINA
	  List<Pedido> buscarPedidosPorEmpleadoClave(String claveEmpleado);
      Pedido buscarPedidoPorIdYEmpleadoClave(Integer idPedido, String claveEmpleado);
}