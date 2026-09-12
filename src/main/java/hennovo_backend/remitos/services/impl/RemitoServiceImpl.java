package hennovo_backend.remitos.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.pedidos.entitys.Pedido;
import hennovo_backend.pedidos.repositorys.PedidoRepository;
import hennovo_backend.precios.entitys.ListaPrecio;
import hennovo_backend.precios.entitys.PrecioProducto;
import hennovo_backend.precios.entitys.UnidadPrecio;
import hennovo_backend.precios.repositorys.ListaPrecioRepository;
import hennovo_backend.precios.repositorys.PrecioProductoRepository;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.productos.repository.ProductoRepository;
import hennovo_backend.productos.util.UnidadesPermitidas;
import hennovo_backend.remitos.dtos.request.DetalleRemitoRequest;
import hennovo_backend.remitos.dtos.request.RemitoRequest;
import hennovo_backend.remitos.dtos.response.DetalleRemitoResponse;
import hennovo_backend.remitos.dtos.response.RemitoResponse;
import hennovo_backend.remitos.entitys.DetalleRemito;
import hennovo_backend.remitos.entitys.Remito;
import hennovo_backend.remitos.mapper.RemitoMapper;
import hennovo_backend.remitos.repositorys.DetalleRemitoRepository;
import hennovo_backend.remitos.repositorys.RemitoRepository;
import hennovo_backend.remitos.services.interfaces.RemitoService;
import hennovo_backend.shared.exception.ConflictException;
import hennovo_backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RemitoServiceImpl implements RemitoService {

        private final RemitoRepository remitoRepository;
        private final DetalleRemitoRepository detalleRemitoRepository;

        private final PedidoRepository pedidoRepository;
        private final ProductoRepository productoRepository;
        private final PrecioProductoRepository precioProductoRepository;
        private final ListaPrecioRepository listaPrecioRepository;

        private final RemitoMapper remitoMapper;

        private static final int MAPLES_POR_CAJON = 12;

        @Override
        public RemitoResponse crear(RemitoRequest request) {

                Pedido pedido = pedidoRepository.findById(request.pedidoId())
                                .orElseThrow(() -> new NotFoundException(
                                                "Pedido no encontrado"));

                if (remitoRepository.findByPedidoId(pedido.getId()).isPresent()) {
                        throw new ConflictException(
                                        "El pedido ya tiene un remito");
                }

                // El pedido debe estar entregado /*revisar este condicional*/
                // if (!pedido.getEntregado()) {
                // throw new BadRequestException(
                // "No se puede generar un remito para un pedido que no fue entregado");
                // }

                ListaPrecio listaVigente = listaPrecioRepository.findByFechaHastaIsNull()
                                .orElseThrow(() -> new NotFoundException(
                                                "No existe una lista de precios vigente"));

                Remito remito = remitoMapper.toEntity(
                                request,
                                pedido);

                remito = remitoRepository.save(remito);

                crearDetalles(
                                remito,
                                request.detalles(),
                                pedido.getCliente(),
                                listaVigente);

                return construirResponse(remito);
        }

        @Override
        @Transactional(readOnly = true)
        public RemitoResponse obtenerPorId(Long id) {

                Remito remito = remitoRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                "Remito no encontrado"));

                return construirResponse(remito);
        }

        @Override
        @Transactional(readOnly = true)
        public List<RemitoResponse> listar() {

                return remitoRepository.findAll()
                                .stream()
                                .map(this::construirResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public RemitoResponse obtenerPorPedido(Long pedidoId) {

                Remito remito = remitoRepository.findByPedidoId(pedidoId)
                                .orElseThrow(() -> new NotFoundException(
                                                "El pedido no tiene un remito"));

                return construirResponse(remito);
        }

        private void crearDetalles(
                        Remito remito,
                        List<DetalleRemitoRequest> detallesRequest,
                        Cliente cliente,
                        ListaPrecio listaVigente) {

                for (DetalleRemitoRequest detalleRequest : detallesRequest) {

                        Producto producto = productoRepository
                                        .findById(detalleRequest.productoId())
                                        .orElseThrow(() -> new NotFoundException(
                                                        "Producto no encontrado: "
                                                                        + detalleRequest.productoId()));
                       UnidadesPermitidas.validar(producto, detalleRequest.unidad()); 

                        PrecioProducto precioProducto = precioProductoRepository
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

                        BigDecimal precioUnitario = obtenerPrecioUnitario(
                                        precioProducto,
                                        detalleRequest.unidad());

                        DetalleRemito detalle = remitoMapper.toDetalleEntity(
                                        detalleRequest,
                                        producto);

                        detalle.setRemito(remito);
                        detalle.setPrecioUnitario(precioUnitario);

                        BigDecimal importe = precioUnitario.multiply(
                                        BigDecimal.valueOf(
                                                        detalleRequest.cantidad()));

                        detalle.setImporte(importe);

                        detalleRemitoRepository.save(detalle);
                }
        }

        private BigDecimal obtenerPrecioUnitario(
                        PrecioProducto precioProducto,
                        UnidadPrecio unidadSolicitada) {

                UnidadPrecio unidadPrecio = precioProducto.getUnidadPrecio();

                if (unidadSolicitada == unidadPrecio) {
                        return precioProducto.getPrecio();
                }

                if (unidadPrecio == UnidadPrecio.CAJON
                                && unidadSolicitada == UnidadPrecio.MAPLE) {

                        return precioProducto.getPrecio()
                                        .divide(
                                                        BigDecimal.valueOf(MAPLES_POR_CAJON),
                                                        2,
                                                        RoundingMode.HALF_UP);
                }

                if (unidadPrecio == UnidadPrecio.MAPLE
                                && unidadSolicitada == UnidadPrecio.CAJON) {

                        return precioProducto.getPrecio()
                                        .multiply(
                                                        BigDecimal.valueOf(MAPLES_POR_CAJON));
                }

                throw new IllegalArgumentException(
                                "No existe una conversión entre "
                                                + unidadPrecio
                                                + " y "
                                                + unidadSolicitada);
        }

        private RemitoResponse construirResponse(Remito remito) {

                List<DetalleRemito> detalles = detalleRemitoRepository.findByRemitoId(
                                remito.getId());

                List<DetalleRemitoResponse> detalleResponses = detalles.stream()
                                .map(remitoMapper::toDetalleResponse)
                                .toList();

                BigDecimal total = detalleResponses.stream()
                                .map(DetalleRemitoResponse::importe)
                                .reduce(
                                                BigDecimal.ZERO,
                                                BigDecimal::add);

                return remitoMapper.toResponse(
                                remito,
                                detalleResponses,
                                total);
        }
}
