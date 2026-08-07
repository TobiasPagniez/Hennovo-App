package hennovo_backend.precios.dtos.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ListaPrecioRequest(
        @NotNull(message = "La fecha desde es obligatoria")
        LocalDate fechaDesde,

        LocalDate fechaHasta,

        @NotEmpty(message = "La lista debe incluir al menos un precio")
        List<@Valid PrecioProductoRequest> precios
) {
}
