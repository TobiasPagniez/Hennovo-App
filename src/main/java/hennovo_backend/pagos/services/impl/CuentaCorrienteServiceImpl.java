package hennovo_backend.pagos.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import jakarta.persistence.EntityNotFoundException;
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
                                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

                List<Pedido> pedidos = pedidoRepository.findByClienteIdOrderByFechaAscIdAsc(clienteId);

                Map<Long, BigDecimal> totalesPorPedido = calcularTotalesPorPedido(clienteId);

                List<Pago> pagos = pagoRepository.findByClienteIdAndAnuladoFalseOrderByFechaAsc(
                                clienteId);

                BigDecimal totalPedidos = BigDecimal.ZERO;
                BigDecimal totalPagos = BigDecimal.ZERO;

                /*
                 * Guardamos cuanto dinero de los pagos todavia
                 * queda disponible para aplicar a los pedidos
                 */
                BigDecimal pagosDisponibles = BigDecimal.ZERO;

                for (Pago pago : pagos) {
                        totalPagos = totalPagos.add(pago.getImporte());
                        pagosDisponibles = pagosDisponibles.add(pago.getImporte());
                }

                /*
                 * Calculamos primero el total de cada pedido.
                 */
                List<PedidoEstadoCuentaDTO> pedidosEstado = new ArrayList<>();

                for (Pedido pedido : pedidos) {

                        BigDecimal totalPedido = totalesPorPedido.getOrDefault(pedido.getId(), BigDecimal.ZERO);

                        totalPedidos = totalPedidos.add(totalPedido);

                        BigDecimal pagadoPedido;
                        BigDecimal pendientePedido;

                        /*
                         * Aplicamos los pagos al pedido mas antiguo
                         * antes de pasar al siguiente.
                         */
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

                        pedidosEstado.add(
                                        new PedidoEstadoCuentaDTO(
                                                        pedido.getId(),
                                                        pedido.getFecha(),
                                                        totalPedido,
                                                        pagadoPedido,
                                                        pendientePedido,
                                                        estado));
                }

                /*
                 * Si despues de pagar todos los pedidos todavía
                 * queda dinero disponible, ese importe es saldo a favor.
                 */
                BigDecimal saldoAFavor = pagosDisponibles;

                /*
                 * Si no hay saldo a favor, calculamos cuánto debe
                 * todavía el cliente.
                 */
                BigDecimal saldoPendiente = totalPedidos.subtract(totalPagos);

                if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                        saldoPendiente = BigDecimal.ZERO;
                }

                /*
                 * Movimientos de la cuenta corriente.
                 */
                List<MovimientoCuentaCorrienteDTO> movimientos = new ArrayList<>();

                // PEDIDOS
                for (Pedido pedido : pedidos) {

                        BigDecimal totalPedido = totalesPorPedido.getOrDefault(pedido.getId(), BigDecimal.ZERO);

                        movimientos.add(
                                        new MovimientoCuentaCorrienteDTO(
                                                        pedido.getFecha(),
                                                        TipoMovimiento.PEDIDO,
                                                        pedido.getId(),
                                                        "Pedido #" + pedido.getId(),
                                                        totalPedido,
                                                        BigDecimal.ZERO));
                }

                // PAGOS
                for (Pago pago : pagos) {

                        movimientos.add(
                                        new MovimientoCuentaCorrienteDTO(
                                                        pago.getFecha(),
                                                        TipoMovimiento.PAGO,
                                                        pago.getId(),
                                                        "Pago - " + pago.getMedioPago(),
                                                        pago.getImporte().negate(),
                                                        BigDecimal.ZERO));
                }

                /*
                 * Orden:
                 * 1. Fecha
                 * 2. Pedido antes que Pago si tienen la misma fecha
                 * 3. ID
                 */
                movimientos.sort(
                                Comparator
                                                .comparing(
                                                                MovimientoCuentaCorrienteDTO::fecha)
                                                .thenComparing(
                                                                movimiento -> movimiento.tipo() == TipoMovimiento.PEDIDO
                                                                                ? 0
                                                                                : 1)
                                                .thenComparing(
                                                                MovimientoCuentaCorrienteDTO::referenciaId));

                /*
                 * Saldo acumulado de movimientos.
                 */
                BigDecimal saldoMovimiento = BigDecimal.ZERO;

                List<MovimientoCuentaCorrienteDTO> movimientosConSaldo = new ArrayList<>();

                for (MovimientoCuentaCorrienteDTO movimiento : movimientos) {

                        saldoMovimiento = saldoMovimiento.add(movimiento.importe());

                        movimientosConSaldo.add(
                                        new MovimientoCuentaCorrienteDTO(
                                                        movimiento.fecha(),
                                                        movimiento.tipo(),
                                                        movimiento.referenciaId(),
                                                        movimiento.descripcion(),
                                                        movimiento.importe(),
                                                        saldoMovimiento));
                }

                return new CuentaCorrienteResponseDTO(
                                cliente.getId(),
                                cliente.getNombre(),
                                totalPedidos,
                                totalPagos,
                                saldoPendiente,
                                saldoAFavor,
                                pedidosEstado,
                                movimientosConSaldo);
        }

        private Map<Long, BigDecimal> calcularTotalesPorPedido(Long clienteId) {

                Map<Long, BigDecimal> totalesPorPedido = new HashMap<>();

                for (DetallePedido detalle : detallePedidoRepository.findByPedidoClienteId(clienteId)) {
                        BigDecimal subtotal = detalle.getPrecioUnitario()
                                        .multiply(BigDecimal.valueOf(detalle.getCantidad()));

                        totalesPorPedido.merge(detalle.getPedido().getId(), subtotal, BigDecimal::add);
                }

                return totalesPorPedido;
        }
}
