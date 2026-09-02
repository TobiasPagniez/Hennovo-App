package hennovo_backend.controlhorario.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import hennovo_backend.controlhorario.dtos.request.ControlHorarioRequest;
import hennovo_backend.controlhorario.dtos.response.ControlHorarioResponse;
import hennovo_backend.controlhorario.dtos.response.ResumenHorasDTO;

public interface ControlHorarioService {

    ControlHorarioResponse crear(ControlHorarioRequest request);

    ControlHorarioResponse actualizar(Long id, ControlHorarioRequest request);

    void eliminar(Long id);

    List<ControlHorarioResponse> listarPropios();

    List<ControlHorarioResponse> listarPorUsuario(Long usuarioId, LocalDate desde, LocalDate hasta);

    List<ControlHorarioResponse> listarTodos(LocalDate desde, LocalDate hasta);

    List<ResumenHorasDTO> resumenPorUsuario(LocalDate desde, LocalDate hasta);
}
