package hennovo_backend.remitos.dtos.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RemitoRequest(

        @NotNull(message = "El pedido es obligatorio")
        Long pedidoId,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "Debe indicar si corresponde a facturación")
        Boolean correspondeFacturacion,

        @NotEmpty(message = "El remito debe tener al menos un detalle")
        List<@Valid DetalleRemitoRequest> detalles

) {
}
