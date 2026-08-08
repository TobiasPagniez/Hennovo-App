package hennovo_backend.pedidos.dtos.response;

import java.math.BigDecimal;

public record DetallePedidoResponse(

        Long id,

        Long productoId,

        Integer cantidad,

        BigDecimal precioUnitario,

        BigDecimal subtotal

) {
}
