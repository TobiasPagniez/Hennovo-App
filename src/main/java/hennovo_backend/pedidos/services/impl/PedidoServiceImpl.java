package hennovo_backend.pedidos.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.auth.entitys.NombreRol;
import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.auth.repositorys.UsuarioRepository;
import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.clientes.repositorys.ClienteRepository;
import hennovo_backend.pedidos.dtos.request.DetallePedidoRequest;
import hennovo_backend.pedidos.dtos.request.PedidoRequest;
import hennovo_backend.pedidos.dtos.response.DetallePedidoResponse;
import hennovo_backend.pedidos.dtos.response.PaginaResponse;
import hennovo_backend.pedidos.dtos.response.PedidoResponse;
import hennovo_backend.pedidos.entitys.DetallePedido;
import hennovo_backend.pedidos.entitys.Pedido;
import hennovo_backend.pedidos.mapper.DetallePedidoMapper;
import hennovo_backend.pedidos.mapper.PedidoMapper;
import hennovo_backend.pedidos.repositorys.DetallePedidoRepository;
import hennovo_backend.pedidos.repositorys.PedidoRepository;
import hennovo_backend.pedidos.services.interfaces.PedidoService;
import hennovo_backend.precios.entitys.ListaPrecio;
import hennovo_backend.precios.entitys.PrecioProducto;
import hennovo_backend.precios.repositorys.ListaPrecioRepository;
import hennovo_backend.precios.repositorys.PrecioProductoRepository;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.productos.repository.ProductoRepository;
import hennovo_backend.shared.exception.BadRequestException;
import hennovo_backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final PrecioProductoRepository precioProductoRepository;
    private final ListaPrecioRepository listaPrecioRepository;
    private final UsuarioRepository usuarioRepository;

    private final PedidoMapper pedidoMapper;
    private final DetallePedidoMapper detallePedidoMapper;

    @Override
    public PedidoResponse crear(PedidoRequest request) {

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        Usuario usuario = obtenerUsuarioParaPedido(request.usuarioId());

        ListaPrecio listaVigente = obtenerListaVigente();

        Pedido pedido = pedidoMapper.toEntity(
                request,
                cliente,
                usuario);

        pedido = pedidoRepository.save(pedido);

        crearDetalles(pedido, request.detalles(), cliente, listaVigente);

        return construirResponse(pedido);
    }

    private Usuario obtenerUsuarioParaPedido(Long usuarioIdSolicitado) {
        if (usuarioIdSolicitado != null) {
            return usuarioRepository.findById(usuarioIdSolicitado)
                    .orElseThrow(() -> new NotFoundException("El usuario indicado para el pedido no existe"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAutenticado = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Usuario autenticado no encontrado"));

        // El admin puede crear pedidos sin asignar; el empleado siempre se autoasigna
        if (usuarioAutenticado.getRol().getNombre() == NombreRol.ADMIN) {
            return null;
        }
        return usuarioAutenticado;
    }

    @Override
    public PedidoResponse asignarUsuario(Long id, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if (!usuario.getActivo()) {
            throw new BadRequestException("El usuario está inactivo");
        }
        pedido.setUsuario(usuario);
        pedidoRepository.save(pedido);
        return construirResponse(pedido);
    }

    private ListaPrecio obtenerListaVigente() {

        return listaPrecioRepository.findByFechaHastaIsNull()
                .orElseThrow(() -> new NotFoundException(
                        "No existe una lista de precios vigente"));
    }

    private PrecioProducto obtenerPrecio(
            Producto producto,
            Cliente cliente,
            ListaPrecio listaVigente) {

        return precioProductoRepository
                .findByProductoIdAndCategoriaIdAndListaId(
                        producto.getId(),
                        cliente.getCategoria().getId(),
                        listaVigente.getId())
                .orElseThrow(() -> new NotFoundException(
                        "No existe un precio para el producto "
                                + producto.getId()
                                + " en la categoría "
                                + cliente.getCategoria().getId()
                                + " dentro de la lista vigente"));
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        return construirResponse(pedido);
    }

    private PedidoResponse construirResponse(Pedido pedido) {

        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getId());

        return construirResponse(pedido, detalles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {

        return construirListaResponse(pedidoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorFecha(LocalDate fecha) {

        return construirListaResponse(pedidoRepository.findByFecha(fecha));
    }

    @Override
    public PaginaResponse<PedidoResponse> listarPaginado(
            LocalDate fecha,
            String buscar,
            int pagina,
            int tamano) {

        if (pagina < 0) {
            pagina = 0;
        }

        if (tamano < 1) {
            tamano = 10;
        }

        if (tamano > 50) {
            tamano = 50;
        }

        Pageable pageable = PageRequest.of(
                pagina,
                tamano,
                Sort.by(
                        Sort.Order.desc("fecha"),
                        Sort.Order.desc("id")));

        String busqueda = buscar == null
                ? ""
                : buscar.trim();

        Page<Pedido> paginaPedidos;

        if (busqueda.isEmpty()) {

            if (fecha != null) {
                paginaPedidos = pedidoRepository.findByFecha(fecha, pageable);
            } else {
                paginaPedidos = pedidoRepository.findAll(pageable);
            }

        } else {

            if (fecha != null) {
                paginaPedidos = pedidoRepository
                        .findByFechaAndClienteNombreContainingIgnoreCase(
                                fecha,
                                busqueda,
                                pageable);
            } else {
                paginaPedidos = pedidoRepository
                        .findByClienteNombreContainingIgnoreCase(
                                busqueda,
                                pageable);
            }
        }

        List<PedidoResponse> contenido = construirListaResponse(paginaPedidos.getContent());

        return new PaginaResponse<>(
                contenido,
                paginaPedidos.getNumber(),
                paginaPedidos.getSize(),
                paginaPedidos.getTotalElements(),
                paginaPedidos.getTotalPages());
    }

    private List<PedidoResponse> construirListaResponse(List<Pedido> pedidos) {

        if (pedidos.isEmpty()) {
            return List.of();
        }

        List<Long> pedidoIds = pedidos.stream()
                .map(Pedido::getId)
                .toList();

        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoIdIn(pedidoIds);

        Map<Long, List<DetallePedido>> detallesPorPedido = detalles.stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getPedido().getId()));

        return pedidos.stream()
                .map(pedido -> construirResponse(
                        pedido,
                        detallesPorPedido.getOrDefault(
                                pedido.getId(),
                                List.of())))
                .toList();
    }

    private PedidoResponse construirResponse(
            Pedido pedido,
            List<DetallePedido> detalles) {

        List<DetallePedidoResponse> detalleResponses = detalles.stream()
                .map(detallePedidoMapper::toResponse)
                .toList();

        BigDecimal total = detalleResponses.stream()
                .map(DetallePedidoResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return pedidoMapper.toResponse(
                pedido,
                detalleResponses,
                total);
    }

    @Override
    public PedidoResponse actualizar(
            Long id,
            PedidoRequest request) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        if (pedido.getEntregado()) {
            throw new BadRequestException(
                    "No se puede modificar un pedido entregado");
        }

        Cliente cliente = clienteRepository.findById(
                request.clienteId()).orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        ListaPrecio listaVigente = obtenerListaVigente();

        pedido.setCliente(cliente);
        pedido.setFecha(request.fecha());
        pedido.setObservaciones(request.observaciones());
        pedido.setBanco(request.banco());

        pedidoRepository.save(pedido);

        detallePedidoRepository.deleteByPedidoId(id);

        crearDetalles(
                pedido,
                request.detalles(),
                cliente,
                listaVigente);

        return construirResponse(pedido);
    }

    private BigDecimal crearDetalles(
            Pedido pedido,
            List<DetallePedidoRequest> detallesRequest,
            Cliente cliente,
            ListaPrecio listaVigente) {

        BigDecimal total = BigDecimal.ZERO;

        for (DetallePedidoRequest detalleRequest : detallesRequest) {

            Producto producto = productoRepository
                    .findById(detalleRequest.productoId())
                    .orElseThrow(() -> new NotFoundException(
                            "Producto no encontrado: "
                                    + detalleRequest.productoId()));

            if (!producto.getActivo()) {
                throw new BadRequestException("No se puede agregar un producto inactivo");
            }

            PrecioProducto precioProducto = obtenerPrecio(
                    producto,
                    cliente,
                    listaVigente);

            BigDecimal precioUnitario = precioProducto.getPrecio();

            DetallePedido detalle = detallePedidoMapper.toEntity(
                    detalleRequest,
                    producto);

            detalle.setPedido(pedido);
            detalle.setPrecioUnitario(precioUnitario);

            detallePedidoRepository.save(detalle);

            BigDecimal subtotal = precioUnitario.multiply(
                    BigDecimal.valueOf(detalleRequest.cantidad()));

            total = total.add(subtotal);
        }

        return total;
    }

    @Override
    public void marcarComoEntregado(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        pedido.setEntregado(true);

        pedidoRepository.save(pedido);
    }

    @Override
    public void marcarComoPagado(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        pedido.setPagado(true);

        pedidoRepository.save(pedido);
    }
}
