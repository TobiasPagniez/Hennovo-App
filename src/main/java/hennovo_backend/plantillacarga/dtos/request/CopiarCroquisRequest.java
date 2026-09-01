package hennovo_backend.plantillacarga.dtos.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record CopiarCroquisRequest(

        @NotNull(message = "La fecha de origen es obligatoria")
        LocalDate fechaOrigen,

        @NotNull(message = "La fecha de destino es obligatoria")
        LocalDate fechaDestino

) {}
