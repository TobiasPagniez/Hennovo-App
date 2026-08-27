package hennovo_backend.planillaventas.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.auth.repositorys.UsuarioRepository;
import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.clientes.repositorys.ClienteRepository;
import hennovo_backend.pagos.dtos.response.CuentaCorrienteResponseDTO;
import hennovo_backend.pagos.services.interfaces.CuentaCorrienteService;
import hennovo_backend.pedidos.entitys.DetallePedido;
import hennovo_backend.pedidos.entitys.Pedido;
import hennovo_backend.pedidos.repositorys.DetallePedidoRepository;
import hennovo_backend.pedidos.repositorys.PedidoRepository;
import hennovo_backend.pedidoshabituales.repository.PedidoHabitualRepository;
import hennovo_backend.planillaventas.dtos.request.PlanillaVentasOrdenItemRequest;
import hennovo_backend.planillaventas.dtos.request.PlanillaVentasOrdenRequest;
import hennovo_backend.planillaventas.dtos.response.CantidadesTamanoDTO;
import hennovo_backend.planillaventas.dtos.response.PlanillaVentasClienteDTO;
import hennovo_backend.planillaventas.dtos.response.TopeProductoDTO;
import hennovo_backend.planillaventas.entitys.PlanillaVentasOrden;
import hennovo_backend.planillaventas.repository.PlanillaVentasOrdenRepository;
import hennovo_backend.planillaventas.services.interfaces.PlanillaVentasService;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanillaVentasServiceImpl implements PlanillaVentasService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoHabitualRepository pedidoHabitualRepository;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final PlanillaVentasOrdenRepository planillaVentasOrdenRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PlanillaVentasClienteDTO> obtenerPlanilla(Long usuarioId, LocalDate fecha) {

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        List<Cliente> clientes = clienteRepository.findAll().stream()
                .filter(Cliente::getActivo)
                .toList();

        // Solo los pedidos de ESTE usuario (dueño de la planilla) para esta fecha
        List<Pedido> pedidosDelDia = pedidoRepository.findByFechaAndUsuarioId(fecha, usuarioId);

        Map<Long, Pedido> pedidoPorCliente = pedidosDelDia.stream()
                .collect(Collectors.toMap(p -> p.getCliente().getId(), p -> p));

        List<Long> pedidoIds = pedidosDelDia.stream().map(Pedido::getId).toList();

        Map<Long, List<DetallePedido>> detallesPorPedido = pedidoIds.isEmpty()
                ? Map.of()
                : detallePedidoRepository.findByPedidoIdIn(pedidoIds).stream()
                        .collect(Collectors.groupingBy(d -> d.getPedido().getId()));

        Map<Long, Integer> ordenPorCliente = planillaVentasOrdenRepository
                .findByUsuarioIdAndFecha(usuarioId, fecha).stream()
                .collect(Collectors.toMap(o -> o.getCliente().getId(), PlanillaVentasOrden::getOrden));

        List<PlanillaVentasClienteDTO> resultado = new ArrayList<>();

        for (Cliente cliente : clientes) {

            Pedido pedido = pedidoPorCliente.get(cliente.getId());

            List<TopeProductoDTO> topes = pedidoHabitualRepository
                    .findByClienteId(cliente.getId())
                    .stream()
                    .map(h -> new TopeProductoDTO(
                            h.getProducto().getId(),
                            nombreProducto(h.getProducto()),
                            h.getCantidad()))
                    .toList();

            CuentaCorrienteResponseDTO cuenta = cuentaCorrienteService
                    .obtenerPorCliente(cliente.getId());

            List<DetallePedido> detalles = pedido == null
                    ? List.of()
                    : detallesPorPedido.getOrDefault(pedido.getId(), List.of());

            CantidadesTamanoDTO cantidades = calcularCantidades(detalles);

            resultado.add(new PlanillaVentasClienteDTO(
                    cliente.getId(),
                    cliente.getNombre(),
                    cliente.getDireccion(),
                    pedido != null ? pedido.getId() : null,
                    pedido != null ? pedido.getEntregado() : null,
                    pedido != null ? pedido.getPagado() : null,
                    pedido != null ? pedido.getBanco() : null,
                    pedido != null ? calcularTotal(detalles) : null,
                    cuenta.saldo(),
                    cuenta.saldoAFavor(),
                    ordenPorCliente.getOrDefault(cliente.getId(), Integer.MAX_VALUE),
                    topes,
                    cantidades
            ));
        }

        // Ordenamos: primero por orden guardado, y a igualdad (sin orden asignado), alfabético
        resultado.sort((a, b) -> {
            int cmp = Integer.compare(a.orden(), b.orden());
            if (cmp != 0) return cmp;
            return a.clienteNombre().compareToIgnoreCase(b.clienteNombre());
        });

        return resultado;
    }

    @Override
    @Transactional
    public void guardarOrden(PlanillaVentasOrdenRequest request) {

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        for (PlanillaVentasOrdenItemRequest item : request.items()) {

            Cliente cliente = clienteRepository.findById(item.clienteId())
                    .orElseThrow(() -> new NotFoundException(
                            "Cliente no encontrado: " + item.clienteId()));

            PlanillaVentasOrden orden = planillaVentasOrdenRepository
                    .findByUsuarioIdAndClienteIdAndFecha(
                            request.usuarioId(), item.clienteId(), request.fecha())
                    .orElseGet(() -> {
                        PlanillaVentasOrden nuevo = new PlanillaVentasOrden();
                        nuevo.setUsuario(usuario);
                        nuevo.setCliente(cliente);
                        nuevo.setFecha(request.fecha());
                        return nuevo;
                    });

            orden.setOrden(item.orden());

            planillaVentasOrdenRepository.save(orden);
        }
    }

    private CantidadesTamanoDTO calcularCantidades(List<DetallePedido> detalles) {

        int t1Color = 0, t1Blanco = 0, t2Color = 0, t2Blanco = 0, t3Color = 0, t3Blanco = 0, otros = 0;

        for (DetallePedido detalle : detalles) {

            Producto producto = detalle.getProducto();
            int cantidad = detalle.getCantidad();
            boolean esColor = producto.getTipoHuevo().name().equals("COLOR");

            switch (producto.getTamaño()) {
                case GRANDE -> {
                    if (esColor) t1Color += cantidad; else t1Blanco += cantidad;
                }
                case MEDIANO -> {
                    if (esColor) t2Color += cantidad; else t2Blanco += cantidad;
                }
                case CHICO -> {
                    if (esColor) t3Color += cantidad; else t3Blanco += cantidad;
                }
                default -> otros += cantidad;
            }
        }

        return new CantidadesTamanoDTO(t1Color, t1Blanco, t2Color, t2Blanco, t3Color, t3Blanco, otros);
    }

    private BigDecimal calcularTotal(List<DetallePedido> detalles) {
        return detalles.stream()
                .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String nombreProducto(Producto producto) {
        return producto.getPresentacion() + " " + producto.getTipoHuevo() + " " + producto.getTamaño();
    }
}
