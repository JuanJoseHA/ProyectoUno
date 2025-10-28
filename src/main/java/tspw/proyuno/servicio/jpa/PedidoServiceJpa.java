package tspw.proyuno.servicio.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tspw.proyuno.dto.PedidoItemDto;
import tspw.proyuno.modelo.Atender;
import tspw.proyuno.modelo.Cliente;
import tspw.proyuno.modelo.DetallePedido;
import tspw.proyuno.modelo.DetallePedidoId;
import tspw.proyuno.modelo.Empleado;
import tspw.proyuno.modelo.Pedido;
import tspw.proyuno.modelo.Producto;
import tspw.proyuno.modelo.Reserva;
import tspw.proyuno.repository.AtenderRepository;
import tspw.proyuno.repository.ClienteRepository;
import tspw.proyuno.repository.EmpleadoRepository;
import tspw.proyuno.repository.PedidoDetalleRepository;
import tspw.proyuno.repository.PedidoRepository;
import tspw.proyuno.repository.ProductoRepository;
import tspw.proyuno.repository.ReservaRepository;
import tspw.proyuno.servicio.IPedidoServicio;

@Service
public class PedidoServiceJpa implements IPedidoServicio {

	@Autowired
    private final PedidoRepository pRepo;
	@Autowired
    private final PedidoDetalleRepository dRepo;
	@Autowired
    private final ClienteRepository cRepo;
	@Autowired
    private final ProductoRepository prRepo;
	@Autowired
	private EmpleadoRepository eRepo; 
	@Autowired
	private AtenderRepository aRepo;
	@Autowired
	private ReservaRepository rRepo;
	

    @Autowired
    public PedidoServiceJpa(PedidoRepository pRepo,
    		PedidoDetalleRepository dRepo,
                            ClienteRepository cRepo,
                            ProductoRepository prRepo) {
        this.pRepo = pRepo;
        this.dRepo = dRepo;
        this.cRepo = cRepo;
        this.prRepo = prRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> mostrarPedidos() {
        return pRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido buscarPorId(Integer idPedido) {
        Optional<Pedido> opt = pRepo.findById(idPedido);
        return pRepo.findById(idPedido).orElse(null);
    }

    @Override
    @Transactional
    public void eliminarPedido(Integer idPedido) {
        // Si en BD ya pusiste ON DELETE CASCADE en detalle_pedido(idpedido),
        // con esto basta. Además, si tu entidad Pedido tiene:
        // @OneToMany(mappedBy="pedido", cascade=ALL, orphanRemoval=true)
        // también borrará por JPA.
        pRepo.deleteById(idPedido);
    }

    @Override
    @Transactional
    public Pedido crearPedido(Integer idCliente, String claveMesero, Integer idReserva, List<PedidoItemDto> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El pedido no contiene productos.");
        }

        Cliente cliente = cRepo.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe: " + idCliente));
        
        Reserva reserva = null;
        if (idReserva != null) {
        	reserva = rRepo.findById(idReserva)
        			.orElseThrow(() -> new IllegalArgumentException("Reserva no existe: " + idReserva));
        }

        // Normaliza cantidades por producto (si el mismo producto se agregó más de una vez)
        Map<Integer, Integer> cantPorProd = new HashMap<>();
        for (PedidoItemDto it : items) {
            if (it.getProductoId() == null || it.getCantidad() == null || it.getCantidad() <= 0) continue;
            cantPorProd.merge(it.getProductoId(), it.getCantidad(), Integer::sum);
        }
        if (cantPorProd.isEmpty()) {
            throw new IllegalArgumentException("Cantidades inválidas.");
        }

        // Encabezado
        Pedido p = new Pedido();
        p.setIdcliente(cliente);
        p.setReserva(reserva);
        p.setTotal(0); // lo recalculamos abajo
        p = pRepo.save(p); // genera idpedido

        // Carga productos
        List<Producto> productos = prRepo.findAllById(cantPorProd.keySet());

        double total = 0.0;
        List<DetallePedido> detalles = new ArrayList<>();

        for (Producto pr : productos) {
            int cant = cantPorProd.get(pr.getIdprod());

            DetallePedido det = new DetallePedido();
            det.setId(new DetallePedidoId(p.getIdpedido(), pr.getIdprod())); // PK compuesta
            det.setPedido(p);
            det.setProducto(pr);
            det.setCantidad(cant);

            detalles.add(det);
            total += pr.getPrecio() * cant;
        }

        if (!detalles.isEmpty()) {
            dRepo.saveAll(detalles);
        }
        
        if (claveMesero != null && !claveMesero.isBlank()) {
            Empleado mesero = eRepo.findById(claveMesero)
                                    .orElseThrow(() -> new IllegalArgumentException("Mesero no existe: " + claveMesero));
            
            Atender atender = new Atender(mesero, p);
            aRepo.save(atender);
        }

        p.setTotal(total);
        return pRepo.save(p);
    }

	@Override
	public Pedido actualizarPedido(Integer idPedido, Integer idCliente, String claveMesero, Integer idReserva, List<PedidoItemDto> items) {
        Pedido p = pRepo.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no existe: " + idPedido));

        Cliente cli = cRepo.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe: " + idCliente));
        p.setIdcliente(cli);
        
        Reserva reserva = null;
        if (idReserva != null) {
            reserva = rRepo.findById(idReserva)
                    .orElseThrow(() -> new IllegalArgumentException("Reserva no existe: " + idReserva));
        }
        p.setReserva(reserva);

        // normalizar cantidades
        Map<Integer,Integer> cantPorProd = new HashMap<>();
        for (PedidoItemDto it : items) {
            if (it.getProductoId()!=null && it.getCantidad()!=null && it.getCantidad()>0)
                cantPorProd.merge(it.getProductoId(), it.getCantidad(), Integer::sum);
        }

        // 1) borrar detalles actuales del pedido
        dRepo.deleteByIdIdpedido(idPedido);  // borra todas las líneas del pedido


        // 2) volver a crear detalles
        double total = 0.0;
        for (Producto pr : prRepo.findAllById(cantPorProd.keySet())) {
            int cant = cantPorProd.get(pr.getIdprod());

            DetallePedido d = new DetallePedido();
            d.setId(new DetallePedidoId(p.getIdpedido(), pr.getIdprod()));
            d.setPedido(p);
            d.setProducto(pr);
            d.setCantidad(cant);
            dRepo.save(d);

            total += pr.getPrecio() * cant;
        }
        
        aRepo.deleteByIdIdpedido(idPedido); // Borra el registro de la tabla 'atender'

        if (claveMesero != null && !claveMesero.isBlank()) {
            Empleado mesero = eRepo.findById(claveMesero)
                                    .orElseThrow(() -> new IllegalArgumentException("Mesero no existe: " + claveMesero));
            
            Atender atender = new Atender(mesero, p);
            aRepo.save(atender);
        }

        p.setTotal(total);
        return pRepo.save(p);
    }
}
