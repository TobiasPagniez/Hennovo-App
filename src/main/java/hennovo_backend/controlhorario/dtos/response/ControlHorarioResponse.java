package hennovo_backend.controlhorario.dtos.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record ControlHorarioResponse(

        Long id,
        LocalDate fecha,
        String turno,
        LocalTime horaIngreso,
        LocalTime horaEgreso,
        Double horasTrabajadas,
        Long usuarioId,
        String usuarioNombre

) {}
