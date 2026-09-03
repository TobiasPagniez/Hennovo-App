package hennovo_backend.gastos.dtos.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GastoRequest(

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotBlank(message = "La categoría es obligatoria")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String categoria,

        @NotBlank(message = "La descripción es obligatoria")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String descripcion,

        @NotNull(message = "El importe es obligatorio")
        @DecimalMin(value = "0.01", message = "El importe debe ser mayor a 0")
        BigDecimal importe,

        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String observaciones

) {}
