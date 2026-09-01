package hennovo_backend.gastos.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoResponse(

        Long id,
        LocalDate fecha,
        String categoria,
        String descripcion,
        BigDecimal importe,
        String observaciones

) {}
