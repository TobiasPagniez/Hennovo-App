package hennovo_backend.precios.dtos.response;

import java.math.BigDecimal;

public record PrecioProductoResponse(
        Long id,
        Long productoId,
        Long categoriaId,
        BigDecimal precio
) {
}
