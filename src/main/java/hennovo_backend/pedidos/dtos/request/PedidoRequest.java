package hennovo_backend.pedidos.dtos.request;

import java.time.LocalDate;
import java.util.List;

import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PedidoRequest(

        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String observaciones,

        String banco,

        Long usuarioId,

        @NotEmpty(message = "El pedido debe tener al menos un producto")
        List<@Valid DetallePedidoRequest> detalles

) {
}
