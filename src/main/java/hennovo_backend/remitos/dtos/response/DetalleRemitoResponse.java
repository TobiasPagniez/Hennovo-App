package hennovo_backend.remitos.dtos.response;

import java.math.BigDecimal;

import hennovo_backend.precios.entitys.UnidadPrecio;

public record DetalleRemitoResponse(

        Long id,

        Long productoId,

        Integer cantidad,

        UnidadPrecio unidad,

        BigDecimal precioUnitario,

        BigDecimal importe

) {
}