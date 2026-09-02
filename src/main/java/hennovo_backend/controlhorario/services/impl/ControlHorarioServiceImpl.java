package hennovo_backend.controlhorario.services.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.auth.repositorys.UsuarioRepository;
import hennovo_backend.controlhorario.dtos.request.ControlHorarioRequest;
import hennovo_backend.controlhorario.dtos.response.ControlHorarioResponse;
import hennovo_backend.controlhorario.dtos.response.ResumenHorasDTO;
import hennovo_backend.controlhorario.entitys.ControlHorario;
import hennovo_backend.controlhorario.mapper.ControlHorarioMapper;
import hennovo_backend.controlhorario.repositorys.ControlHorarioRepository;
import hennovo_backend.controlhorario.services.interfaces.ControlHorarioService;
import hennovo_backend.shared.exception.NotFoundException;
import hennovo_backend.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ControlHorarioServiceImpl implements ControlHorarioService {

    private final ControlHorarioRepository controlHorarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ControlHorarioMapper controlHorarioMapper;

    @Override
    @Transactional
    public ControlHorarioResponse crear(ControlHorarioRequest request) {

        Usuario usuario = obtenerUsuarioAutenticado();

        ControlHorario control = controlHorarioMapper.toEntity(request, usuario);

        return controlHorarioMapper.toResponse(controlHorarioRepository.save(control));
    }

    @Override
    @Transactional
    public ControlHorarioResponse actualizar(Long id, ControlHorarioRequest request) {

        ControlHorario control = obtenerControl(id);

        Usuario usuarioActual = obtenerUsuarioAutenticado();

        if (!control.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new UnauthorizedException(
                    "No podés modificar el registro de horario de otro usuario");
        }

        controlHorarioMapper.actualizarEntity(control, request);

        return controlHorarioMapper.toResponse(controlHorarioRepository.save(control));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        ControlHorario control = obtenerControl(id);

        Usuario usuarioActual = obtenerUsuarioAutenticado();

        if (!control.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new UnauthorizedException(
                    "No podés eliminar el registro de horario de otro usuario");
        }

        controlHorarioRepository.delete(control);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ControlHorarioResponse> listarPropios() {

        Usuario usuario = obtenerUsuarioAutenticado();

        return controlHorarioRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(controlHorarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ControlHorarioResponse> listarPorUsuario(
            Long usuarioId, LocalDate desde, LocalDate hasta) {

        return controlHorarioRepository
                .findByUsuarioIdAndFechaBetween(usuarioId, desde, hasta)
                .stream()
                .map(controlHorarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ControlHorarioResponse> listarTodos(LocalDate desde, LocalDate hasta) {

        return controlHorarioRepository.findByFechaBetween(desde, hasta)
                .stream()
                .map(controlHorarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumenHorasDTO> resumenPorUsuario(LocalDate desde, LocalDate hasta) {

        List<ControlHorario> registros = controlHorarioRepository.findByFechaBetween(desde, hasta);

        Map<Long, List<ControlHorario>> porUsuario = registros.stream()
                .collect(Collectors.groupingBy(c -> c.getUsuario().getId()));

        return porUsuario.values().stream()
                .map(lista -> {
                    Usuario usuario = lista.get(0).getUsuario();
                    double total = lista.stream()
                            .mapToDouble(ControlHorario::getHorasTrabajadas)
                            .sum();
                    return new ResumenHorasDTO(
                            usuario.getId(),
                            usuario.getNombre() + " " + usuario.getApellido(),
                            Math.round(total * 100.0) / 100.0
                    );
                })
                .sorted((a, b) -> a.usuarioNombre().compareToIgnoreCase(b.usuarioNombre()))
                .toList();
    }

    private ControlHorario obtenerControl(Long id) {
        return controlHorarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Registro de horario no encontrado"));
    }

    private Usuario obtenerUsuarioAutenticado() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario autenticado no encontrado"));
    }
}
