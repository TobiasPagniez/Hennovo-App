package hennovo_backend.pedidos.dtos.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PedidoRequest(

        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        String observaciones,

        @NotEmpty(message = "El pedido debe tener al menos un producto")
        List<@Valid DetallePedidoRequest> detalles

) {
}
