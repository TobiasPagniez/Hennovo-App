package hennovo_backend.pedidoshabituales.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PedidoHabitualRequestDTO(

        @NotNull(message = "El cliente es obligatorio")
        Long idCliente,

        @NotNull(message = "El producto es obligatorio")
        Long idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer cantidad) {
}