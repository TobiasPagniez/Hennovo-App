package hennovo_backend.pagos.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.clientes.repositorys.ClienteRepository;
import hennovo_backend.pagos.dtos.response.CuentaCorrienteResponseDTO;
import hennovo_backend.pagos.dtos.response.MovimientoCuentaCorrienteDTO;
import hennovo_backend.pagos.dtos.response.TipoMovimiento;
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
                .orElseThrow(() ->
                        new EntityNotFoundException("Cliente no encontrado"));

        List<Pedido> pedidos =
                pedidoRepository.findByClienteIdOrderByFechaAscIdAsc(clienteId);

        List<Pago> pagos =
                pagoRepository.findByClienteIdAndAnuladoFalseOrderByFechaAsc(
                        clienteId);

        BigDecimal totalPedidos = BigDecimal.ZERO;
        BigDecimal totalPagos = BigDecimal.ZERO;

        List<MovimientoCuentaCorrienteDTO> movimientos =
                new ArrayList<>();

        //Pedidos
        for (Pedido pedido : pedidos) {

            BigDecimal totalPedido =
                    calcularTotalPedido(pedido.getId());

            totalPedidos = totalPedidos.add(totalPedido);

            movimientos.add(
                    new MovimientoCuentaCorrienteDTO(
                            pedido.getFecha(),
                            TipoMovimiento.PEDIDO,
                            pedido.getId(),
                            "Pedido #" + pedido.getId(),
                            totalPedido,
                            BigDecimal.ZERO
                    )
            );
        }

        // pagos 
        for (Pago pago : pagos) {

            totalPagos = totalPagos.add(pago.getImporte());

            movimientos.add(
                    new MovimientoCuentaCorrienteDTO(
                            pago.getFecha(),
                            TipoMovimiento.PAGO,
                            pago.getId(),
                            "Pago - " + pago.getMedioPago(),
                            pago.getImporte().negate(),
                            BigDecimal.ZERO
                    )
            );
        }

        //ordenar todos los movimientos juntos 
        movimientos.sort(
                Comparator
                        .comparing(
                                MovimientoCuentaCorrienteDTO::fecha)
                        .thenComparing(
                                MovimientoCuentaCorrienteDTO::tipo)
                        .thenComparing(
                                MovimientoCuentaCorrienteDTO::referenciaId)
        );

        //calcular saldo acumulado
        BigDecimal saldo = BigDecimal.ZERO;

        List<MovimientoCuentaCorrienteDTO> movimientosConSaldo =
                new ArrayList<>();

        for (MovimientoCuentaCorrienteDTO movimiento : movimientos) {

            saldo = saldo.add(movimiento.importe());

            movimientosConSaldo.add(
                    new MovimientoCuentaCorrienteDTO(
                            movimiento.fecha(),
                            movimiento.tipo(),
                            movimiento.referenciaId(),
                            movimiento.descripcion(),
                            movimiento.importe(),
                            saldo
                    )
            );
        }

        boolean saldoAFavor =
                saldo.compareTo(BigDecimal.ZERO) < 0;

        return new CuentaCorrienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                totalPedidos,
                totalPagos,
                saldo,
                saldoAFavor,
                movimientosConSaldo
        );
    }

    private BigDecimal calcularTotalPedido(Long pedidoId) {

        List<DetallePedido> detalles =
                detallePedidoRepository.findByPedidoId(pedidoId);

        return detalles.stream()
                .map(detalle ->
                        detalle.getPrecioUnitario()
                                .multiply(
                                        BigDecimal.valueOf(
                                                detalle.getCantidad()
                                        )
                                )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
}