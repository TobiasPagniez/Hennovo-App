package hennovo_backend.controlhorario.dtos.request;

import java.time.LocalDate;
import java.time.LocalTime;

import hennovo_backend.controlhorario.entitys.Turno;
import jakarta.validation.constraints.NotNull;

public record ControlHorarioRequest(

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "El turno es obligatorio")
        Turno turno,

        @NotNull(message = "La hora de ingreso es obligatoria")
        LocalTime horaIngreso,

        @NotNull(message = "La hora de egreso es obligatoria")
        LocalTime horaEgreso

) {}
