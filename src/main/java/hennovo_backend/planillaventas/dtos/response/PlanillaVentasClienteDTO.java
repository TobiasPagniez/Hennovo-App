package hennovo_backend.planillaventas.dtos.response;

import java.math.BigDecimal;
import java.util.List;

public record PlanillaVentasClienteDTO(
        Long clienteId,
        String clienteNombre,
        String direccion,
        Long pedidoId,
        // Todos los pedidos del cliente en el día (normalmente uno solo)
        List<Long> pedidoIds,
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
