package hennovo_backend.gastos.dtos.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GastoRequest(

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotBlank(message = "La categoría es obligatoria")
        String categoria,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotNull(message = "El importe es obligatorio")
        @DecimalMin(value = "0.01", message = "El importe debe ser mayor a 0")
        BigDecimal importe,

        String observaciones

) {}
