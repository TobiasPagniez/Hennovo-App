package hennovo_backend.pagos.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import hennovo_backend.pagos.entitys.EstadoPagoPedido;

public record PedidoEstadoCuentaDTO(

        Long pedidoId,

        LocalDate fecha,

        BigDecimal total,

        BigDecimal pagado,

        BigDecimal pendiente,

        EstadoPagoPedido estado

) {
}
