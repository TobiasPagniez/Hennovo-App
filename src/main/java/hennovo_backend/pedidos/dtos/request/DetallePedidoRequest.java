package hennovo_backend.pedidos.dtos.request;

import hennovo_backend.precios.entitys.UnidadPrecio;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public record DetallePedidoRequest(

        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer cantidad,

        @NotNull(message = "La unidad es obligatoria")
        UnidadPrecio unidad

) {
}
