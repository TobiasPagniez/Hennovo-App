package hennovo_backend.perdidas.dtos.request;

import java.time.LocalDate;

import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PerdidaRequest(

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer cantidad,

        @NotBlank(message = "El motivo es obligatorio")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String motivo,

        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String observaciones

) {}
