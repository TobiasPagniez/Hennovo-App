package hennovo_backend.plantillacarga.dtos.request;

import jakarta.validation.constraints.NotNull;

public record MoverDetalleCeldaRequest(

        @NotNull(message = "La celda de destino es obligatoria")
        Long celdaDestinoId

) {}
