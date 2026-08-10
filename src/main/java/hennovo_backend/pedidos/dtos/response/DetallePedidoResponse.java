package hennovo_backend.pedidos.dtos.response;

import java.math.BigDecimal;

import hennovo_backend.precios.entitys.UnidadPrecio;

public record DetallePedidoResponse(

        Long id,

        Long productoId,

        Integer cantidad,

        UnidadPrecio unidad,

        BigDecimal precioUnitario,

        BigDecimal subtotal

) {
}
