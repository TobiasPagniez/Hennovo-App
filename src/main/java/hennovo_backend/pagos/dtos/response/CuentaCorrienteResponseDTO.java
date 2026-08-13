package hennovo_backend.pagos.dtos.response;

import java.math.BigDecimal;
import java.util.List;

public record CuentaCorrienteResponseDTO(

        Long clienteId,

        String clienteNombre,

        BigDecimal totalPedidos,

        BigDecimal totalPagos,

        BigDecimal saldo,

        Boolean saldoAFavor,

        List<MovimientoCuentaCorrienteDTO> movimientos

) {
}
