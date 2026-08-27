package hennovo_backend.planillaventas.dtos.response;

import java.math.BigDecimal;
import java.util.List;

public record PlanillaVentasClienteDTO(
        Long clienteId,
        String clienteNombre,
        String direccion,
        Long pedidoId,
        Boolean entregado,
        Boolean pagado,
        String banco,
        BigDecimal totalPedido,
        BigDecimal saldoPendiente,
        BigDecimal saldoAFavor,
        Integer orden,
        List<TopeProductoDTO> topes,
        CantidadesTamanoDTO cantidades
) {}
