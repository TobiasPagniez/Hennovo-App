package hennovo_backend.remitos.dtos.response;

import java.math.BigDecimal;

public record DetalleRemitoResponse(

        Long id,

        Long productoId,

        Integer cantidad,

        BigDecimal precioUnitario,

        BigDecimal importe

) {
}