package hennovo_backend.precios.dtos.response;

import java.math.BigDecimal;

import hennovo_backend.precios.entitys.UnidadPrecio;

public record PrecioProductoResponse(
        Long id,
        Long productoId,
        Long categoriaId,
        BigDecimal precio,
        UnidadPrecio unidadPrecio
) {
}
