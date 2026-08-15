package hennovo_backend.remitos.dtos.request;

import hennovo_backend.precios.entitys.UnidadPrecio;

public record DetalleRemitoRequest(

        Long productoId,

        Integer cantidad,

        UnidadPrecio unidad

) {
}
