package hennovo_backend.pagos.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoCuentaCorrienteDTO(

        LocalDate fecha,

        TipoMovimiento tipo,

        Long referenciaId,

        String descripcion,

        BigDecimal importe,

        BigDecimal saldo

) {
}
