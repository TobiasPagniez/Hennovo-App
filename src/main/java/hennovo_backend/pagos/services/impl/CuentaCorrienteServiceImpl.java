package hennovo_backend.pagos.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.clientes.repositorys.ClienteRepository;
import hennovo_backend.pagos.dtos.response.CuentaCorrienteResponseDTO;
import hennovo_backend.pagos.dtos.response.MovimientoCuentaCorrienteDTO;
import hennovo_backend.pagos.dtos.response.PedidoEstadoCuentaDTO;
import hennovo_backend.pagos.dtos.response.TipoMovimiento;
import hennovo_backend.pagos.entitys.EstadoPagoPedido;
import hennovo_backend.pagos.entitys.Pago;
import hennovo_backend.pagos.repositorys.PagoRepository;
import hennovo_backend.pagos.services.interfaces.CuentaCorrienteService;
import hennovo_backend.pedidos.entitys.DetallePedido;
import hennovo_backend.pedidos.entitys.Pedido;
import hennovo_backend.pedidos.repositorys.DetallePedidoRepository;
import hennovo_backend.pedidos.repositorys.PedidoRepository;
import hennovo_backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CuentaCorrienteServiceImpl implements CuentaCorrienteService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PagoRepository pagoRepository;

    @Override
    public CuentaCorrienteResponseDTO obtenerPorCliente(Long clienteId) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        List<Pedido> pedidos = pedidoRepository.findByClienteIdOrderByFechaAscIdAsc(clienteId);
        List<Pago> pagos = pagoRepository.findByClienteIdAndAnuladoFalseOrderByFechaAsc(clienteId);

        BigDecimal totalPagos = pagos.stream()
                .map(Pago::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PedidoEstadoCuentaDTO> pedidosEstado = calcularEstadosPedidos(pedidos, pagos);

        BigDecimal totalPedidos = pedidosEstado.stream()
                .map(PedidoEstadoCuentaDTO::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pagosAplicados = pedidosEstado.stream()
                .map(PedidoEstadoCuentaDTO::pagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Saldo a favor: pagos que sobraron después de cubrir todos los pedidos
        BigDecimal saldoAFavor = totalPagos.subtract(pagosAplicados);
        if (saldoAFavor.compareTo(BigDecimal.ZERO) < 0) {
            saldoAFavor = BigDecimal.ZERO;
        }

        BigDecimal saldoPendiente = totalPedidos.subtract(totalPagos);
        if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
            saldoPendiente = BigDecimal.ZERO;
        }

        List<MovimientoCuentaCorrienteDTO> movimientos = construirMovimientos(pedidosEstado, pedidos, pagos);

        return new CuentaCorrienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                totalPedidos,
                totalPagos,
                saldoPendiente,
                saldoAFavor,
                pedidosEstado,
                movimientos);
    }

    /**
     * Recalcula, con la misma lógica FIFO que obtenerPorCliente(), el estado
     * de cada pedido del cliente y sincroniza Pedido.pagado con ese resultado.
     * Se debe llamar cada vez que se registra o anula un Pago.
     */
    @Override
    @Transactional
    public void sincronizarEstadoPedidos(Long clienteId) {

        List<Pedido> pedidos = pedidoRepository.findByClienteIdOrderByFechaAscIdAsc(clienteId);
        List<Pago> pagos = pagoRepository.findByClienteIdAndAnuladoFalseOrderByFechaAsc(clienteId);

        Map<Long, EstadoPagoPedido> estadoPorPedidoId = calcularEstadosPedidos(pedidos, pagos)
                .stream()
                .collect(Collectors.toMap(
                        PedidoEstadoCuentaDTO::pedidoId,
                        PedidoEstadoCuentaDTO::estado));

        for (Pedido pedido : pedidos) {

            boolean debeEstarPagado = estadoPorPedidoId
                    .getOrDefault(pedido.getId(), EstadoPagoPedido.PENDIENTE) == EstadoPagoPedido.PAGADO;

            if (debeEstarPagado != pedido.getPagado()) {
                pedido.setPagado(debeEstarPagado);
                pedidoRepository.save(pedido);
            }
        }
    }

    /**
     * Aplica los pagos vigentes a los pedidos en orden FIFO (pedido más
     * antiguo primero) y devuelve el estado resultante de cada uno.
     */
    private List<PedidoEstadoCuentaDTO> calcularEstadosPedidos(
            List<Pedido> pedidos,
            List<Pago> pagos) {

        Map<Long, BigDecimal> totalesPorPedido = calcularTotalesPorPedido(pedidos);

        BigDecimal pagosDisponibles = pagos.stream()
                .map(Pago::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PedidoEstadoCuentaDTO> pedidosEstado = new ArrayList<>();

        for (Pedido pedido : pedidos) {

            BigDecimal totalPedido = totalesPorPedido.getOrDefault(pedido.getId(), BigDecimal.ZERO);

            BigDecimal pagadoPedido;
            BigDecimal pendientePedido;

            if (pagosDisponibles.compareTo(BigDecimal.ZERO) <= 0) {

                pagadoPedido = BigDecimal.ZERO;
                pendientePedido = totalPedido;

            } else if (pagosDisponibles.compareTo(totalPedido) >= 0) {

                pagadoPedido = totalPedido;
                pendientePedido = BigDecimal.ZERO;
                pagosDisponibles = pagosDisponibles.subtract(totalPedido);

            } else {

                pagadoPedido = pagosDisponibles;
                pendientePedido = totalPedido.subtract(pagosDisponibles);
                pagosDisponibles = BigDecimal.ZERO;
            }

            EstadoPagoPedido estado;

            if (pagadoPedido.compareTo(BigDecimal.ZERO) == 0) {
                estado = EstadoPagoPedido.PENDIENTE;
            } else if (pendientePedido.compareTo(BigDecimal.ZERO) == 0) {
                estado = EstadoPagoPedido.PAGADO;
            } else {
                estado = EstadoPagoPedido.PARCIAL;
            }

            pedidosEstado.add(new PedidoEstadoCuentaDTO(
                    pedido.getId(),
                    pedido.getFecha(),
                    totalPedido,
                    pagadoPedido,
                    pendientePedido,
                    estado));
        }

        return pedidosEstado;
    }

    private List<MovimientoCuentaCorrienteDTO> construirMovimientos(
            List<PedidoEstadoCuentaDTO> pedidosEstado,
            List<Pedido> pedidos,
            List<Pago> pagos) {

        List<MovimientoCuentaCorrienteDTO> movimientos = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            BigDecimal totalPedido = pedidosEstado.stream()
                    .filter(pe -> pe.pedidoId().equals(pedido.getId()))
                    .map(PedidoEstadoCuentaDTO::total)
                    .findFirst()
                    .orElse(BigDecimal.ZERO);

            movimientos.add(new MovimientoCuentaCorrienteDTO(
                    pedido.getFecha(),
                    TipoMovimiento.PEDIDO,
                    pedido.getId(),
                    "Pedido #" + pedido.getId(),
                    totalPedido,
                    BigDecimal.ZERO));
        }

        for (Pago pago : pagos) {
            movimientos.add(new MovimientoCuentaCorrienteDTO(
                    pago.getFecha(),
                    TipoMovimiento.PAGO,
                    pago.getId(),
                    "Pago - " + pago.getMedioPago(),
                    pago.getImporte().negate(),
                    BigDecimal.ZERO));
        }

        movimientos.sort(
                Comparator.comparing(MovimientoCuentaCorrienteDTO::fecha)
                        .thenComparing(m -> m.tipo() == TipoMovimiento.PEDIDO ? 0 : 1)
                        .thenComparing(MovimientoCuentaCorrienteDTO::referenciaId));

        BigDecimal saldoMovimiento = BigDecimal.ZERO;
        List<MovimientoCuentaCorrienteDTO> movimientosConSaldo = new ArrayList<>();

        for (MovimientoCuentaCorrienteDTO movimiento : movimientos) {
            saldoMovimiento = saldoMovimiento.add(movimiento.importe());
            movimientosConSaldo.add(new MovimientoCuentaCorrienteDTO(
                    movimiento.fecha(),
                    movimiento.tipo(),
                    movimiento.referenciaId(),
                    movimiento.descripcion(),
                    movimiento.importe(),
                    saldoMovimiento));
        }

        return movimientosConSaldo;
    }

    private Map<Long, BigDecimal> calcularTotalesPorPedido(List<Pedido> pedidos) {

        Map<Long, BigDecimal> totalesPorPedido = new HashMap<>();

        if (pedidos.isEmpty()) {
            return totalesPorPedido;
        }

        for (DetallePedido detalle : detallePedidoRepository
                .findByPedidoClienteId(pedidos.get(0).getCliente().getId())) {
            BigDecimal subtotal = detalle.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));

            totalesPorPedido.merge(detalle.getPedido().getId(), subtotal, BigDecimal::add);
        }

        return totalesPorPedido;
    }
}
