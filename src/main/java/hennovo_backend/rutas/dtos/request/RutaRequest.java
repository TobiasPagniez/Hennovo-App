package hennovo_backend.rutas.dtos.request;

import java.time.LocalDate;

import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RutaRequest(

        @NotNull(message = "La fecha es obligatoria") LocalDate fecha,

        @NotBlank(message = "El nombre de la ruta es obligatorio") @Size(max = 100, message = "El nombre no puede superar los 100 caracteres") @NoHtml(message = "No se permiten etiquetas HTML en este campo") String nombre,

        @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres") @NoHtml(message = "No se permiten etiquetas HTML en este campo") String observaciones,

        @NotNull(message = "El empleado es obligatorio") Long usuarioId,

        @NotNull(message = "El vehículo es obligatorio") Long vehiculoId

) {
}
