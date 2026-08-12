package hennovo_backend.rutas.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrdenPedidoRequest(

        @NotNull(message = "El pedido es obligatorio")
        Long pedidoId,

        @NotNull(message = "El orden es obligatorio")
        @Min(value = 1, message = "El orden debe ser mayor a 0")
        Integer orden

) {
}