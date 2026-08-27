package hennovo_backend.planillaventas.dtos.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PlanillaVentasOrdenRequest(

        @NotNull(message = "El usuario es obligatorio")
        Long usuarioId,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotEmpty(message = "Debe indicar al menos un cliente")
        List<@Valid PlanillaVentasOrdenItemRequest> items

) {}
