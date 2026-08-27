package hennovo_backend.planillaventas.dtos.request;

import jakarta.validation.constraints.NotNull;

public record PlanillaVentasOrdenItemRequest(

        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "El orden es obligatorio")
        Integer orden

) {}
