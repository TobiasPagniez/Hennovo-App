package hennovo_backend.controlhorario.mapper;

import java.time.Duration;

import org.springframework.stereotype.Component;

import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.controlhorario.dtos.request.ControlHorarioRequest;
import hennovo_backend.controlhorario.dtos.response.ControlHorarioResponse;
import hennovo_backend.controlhorario.entitys.ControlHorario;
import hennovo_backend.shared.exception.BadRequestException;

@Component
public class ControlHorarioMapper {

    public ControlHorario toEntity(ControlHorarioRequest request, Usuario usuario) {

        ControlHorario control = new ControlHorario();

        control.setFecha(request.fecha());
        control.setTurno(request.turno());
        control.setHoraIngreso(request.horaIngreso());
        control.setHoraEgreso(request.horaEgreso());
        control.setHorasTrabajadas(calcularHoras(request.horaIngreso(), request.horaEgreso()));
        control.setUsuario(usuario);

        return control;
    }

    public void actualizarEntity(ControlHorario control, ControlHorarioRequest request) {

        control.setFecha(request.fecha());
        control.setTurno(request.turno());
        control.setHoraIngreso(request.horaIngreso());
        control.setHoraEgreso(request.horaEgreso());
        control.setHorasTrabajadas(calcularHoras(request.horaIngreso(), request.horaEgreso()));
    }

    private Double calcularHoras(java.time.LocalTime ingreso, java.time.LocalTime egreso) {

        if (!egreso.isAfter(ingreso)) {
            throw new BadRequestException(
                    "La hora de egreso debe ser posterior a la hora de ingreso");
        }

        long minutos = Duration.between(ingreso, egreso).toMinutes();

        return Math.round((minutos / 60.0) * 100.0) / 100.0;
    }

    public ControlHorarioResponse toResponse(ControlHorario control) {

        return new ControlHorarioResponse(
                control.getId(),
                control.getFecha(),
                control.getTurno().name(),
                control.getHoraIngreso(),
                control.getHoraEgreso(),
                control.getHorasTrabajadas(),
                control.getUsuario().getId(),
                control.getUsuario().getNombre() + " " + control.getUsuario().getApellido()
        );
    }
}
