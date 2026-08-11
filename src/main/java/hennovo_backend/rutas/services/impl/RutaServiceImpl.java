package hennovo_backend.rutas.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.auth.entitys.NombreRol;
import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.auth.repositorys.UsuarioRepository;
import hennovo_backend.rutas.dtos.request.RutaRequest;
import hennovo_backend.rutas.dtos.response.RutaResponse;
import hennovo_backend.rutas.entitys.Ruta;
import hennovo_backend.rutas.mapper.RutaMapper;
import hennovo_backend.rutas.repositorys.RutaRepository;
import hennovo_backend.rutas.services.interfaces.RutaService;
import hennovo_backend.shared.exception.BadRequestException;
import hennovo_backend.shared.exception.NotFoundException;
import hennovo_backend.vehiculos.entitys.Vehiculo;
import hennovo_backend.vehiculos.repositorys.VehiculoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RutaServiceImpl implements RutaService {

    private final RutaRepository rutaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;

    private final RutaMapper rutaMapper;

    @Override
    public RutaResponse crear(RutaRequest request) {

        Usuario usuario = obtenerUsuarioActivo(request.usuarioId());

        Vehiculo vehiculo = obtenerVehiculoActivo(request.vehiculoId());

        Ruta ruta = rutaMapper.toEntity(
                request,
                usuario,
                vehiculo);

        ruta = rutaRepository.save(ruta);

        return rutaMapper.toResponse(ruta);
    }

    @Override
    @Transactional(readOnly = true)
    public RutaResponse obtenerPorId(Long id) {

        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        return rutaMapper.toResponse(ruta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutaResponse> listar() {

        return rutaRepository.findAll()
                .stream()
                .map(rutaMapper::toResponse)
                .toList();
    }

    @Override
    public RutaResponse actualizar(
            Long id,
            RutaRequest request) {

        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        if (!ruta.getActiva()) {
            throw new BadRequestException(
                    "No se puede modificar una ruta inactiva");
        }

        Usuario usuario = obtenerUsuarioActivo(request.usuarioId());

        Vehiculo vehiculo = obtenerVehiculoActivo(request.vehiculoId());

        ruta.setFecha(request.fecha());
        ruta.setNombre(request.nombre());
        ruta.setObservaciones(request.observaciones());
        ruta.setUsuario(usuario);
        ruta.setVehiculo(vehiculo);

        ruta = rutaRepository.save(ruta);

        return rutaMapper.toResponse(ruta);
    }

    @Override
    public void desactivar(Long id) {

        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        ruta.setActiva(false);

        rutaRepository.save(ruta);
    }

    private Usuario obtenerUsuarioActivo(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException(
                        "Empleado no encontrado"));

        if (!usuario.getActivo()) {
            throw new BadRequestException(
                    "El empleado está inactivo");
        }

        if (usuario.getRol().getNombre() != NombreRol.EMPLEADO) {
            throw new BadRequestException(
                    "La ruta debe estar asignada a un empleado");
        }

        return usuario;
    }

    private Vehiculo obtenerVehiculoActivo(Long vehiculoId) {

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new NotFoundException(
                        "Vehículo no encontrado"));

        if (!vehiculo.getActivo()) {
            throw new BadRequestException(
                    "El vehículo está inactivo");
        }

        return vehiculo;
    }
}
