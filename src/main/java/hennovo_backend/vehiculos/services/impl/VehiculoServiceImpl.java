package hennovo_backend.vehiculos.services.impl;

import hennovo_backend.shared.exception.BadRequestException;
import hennovo_backend.shared.exception.ConflictException;
import hennovo_backend.shared.exception.NotFoundException;
import hennovo_backend.vehiculos.dtos.request.ActualizarKilometrajeRequest;
import hennovo_backend.vehiculos.dtos.request.CreateVehiculoRequest;
import hennovo_backend.vehiculos.dtos.request.UpdateVehiculoRequest;
import hennovo_backend.vehiculos.dtos.response.KilometrajeHistorialResponse;
import hennovo_backend.vehiculos.dtos.response.VehiculoResponse;
import hennovo_backend.vehiculos.entitys.KilometrajeHistorial;
import hennovo_backend.vehiculos.entitys.Vehiculo;
import hennovo_backend.vehiculos.mapper.VehiculoMapper;
import hennovo_backend.vehiculos.repositorys.KilometrajeHistorialRepository;
import hennovo_backend.vehiculos.repositorys.VehiculoRepository;
import hennovo_backend.vehiculos.services.interfaces.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final KilometrajeHistorialRepository kilometrajeHistorialRepository;
    private final VehiculoMapper vehiculoMapper;

    @Override
    @Transactional
    public VehiculoResponse crear(CreateVehiculoRequest request) {
        if (vehiculoRepository.existsByPatente(request.getPatente())) {
            throw new ConflictException("Ya existe un vehículo registrado con la patente " + request.getPatente());
        }

        Vehiculo vehiculo = vehiculoMapper.toEntity(request);
        Vehiculo guardado = vehiculoRepository.save(vehiculo);

        // Dejamos asentado el kilometraje inicial en el historial
        registrarKilometraje(guardado, guardado.getKilometrajeActual(), "Kilometraje inicial");

        return vehiculoMapper.toResponse(guardado);
    }

    @Override
    public List<VehiculoResponse> listar() {
        return vehiculoRepository.findByActivoTrue().stream()
                .map(vehiculoMapper::toResponse)
                .toList();
    }

    @Override
    public VehiculoResponse buscarPorId(Long id) {
        return vehiculoMapper.toResponse(obtenerVehiculo(id));
    }

    @Override
    @Transactional
    public VehiculoResponse actualizar(Long id, UpdateVehiculoRequest request) {
        Vehiculo vehiculo = obtenerVehiculo(id);

        if (request.getPatente() != null && !request.getPatente().equals(vehiculo.getPatente())
                && vehiculoRepository.existsByPatente(request.getPatente())) {
            throw new ConflictException("Ya existe un vehículo registrado con la patente " + request.getPatente());
        }

        if (request.getPatente() != null) vehiculo.setPatente(request.getPatente());
        if (request.getMarca() != null) vehiculo.setMarca(request.getMarca());
        if (request.getModelo() != null) vehiculo.setModelo(request.getModelo());
        if (request.getAnio() != null) vehiculo.setAnio(request.getAnio());
        if (request.getProximoServiceFecha() != null) vehiculo.setProximoServiceFecha(request.getProximoServiceFecha());
        if (request.getProximoCambioAceiteFecha() != null) vehiculo.setProximoCambioAceiteFecha(request.getProximoCambioAceiteFecha());
        if (request.getObservacionesMantenimiento() != null) vehiculo.setObservacionesMantenimiento(request.getObservacionesMantenimiento());

        return vehiculoMapper.toResponse(vehiculoRepository.save(vehiculo));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Vehiculo vehiculo = obtenerVehiculo(id);
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);
    }

    @Override
    @Transactional
    public VehiculoResponse actualizarKilometraje(Long id, ActualizarKilometrajeRequest request) {
        Vehiculo vehiculo = obtenerVehiculo(id);

        if (request.getKilometraje() < vehiculo.getKilometrajeActual()) {
            throw new BadRequestException(
                    "El nuevo kilometraje no puede ser menor al kilometraje actual (" + vehiculo.getKilometrajeActual() + ")");
        }

        vehiculo.setKilometrajeActual(request.getKilometraje());
        vehiculoRepository.save(vehiculo);

        registrarKilometraje(vehiculo, request.getKilometraje(), request.getObservacion());

        return vehiculoMapper.toResponse(vehiculo);
    }

    @Override
    public List<KilometrajeHistorialResponse> historialKilometraje(Long id) {
        obtenerVehiculo(id); // valida que exista antes de traer el historial
        return kilometrajeHistorialRepository.findByVehiculo_IdOrderByFechaDesc(id).stream()
                .map(vehiculoMapper::toResponse)
                .toList();
    }

    @Override
    public List<VehiculoResponse> consultarVencimientos(int diasAnticipacion) {
        LocalDate limite = LocalDate.now().plusDays(diasAnticipacion);

        return vehiculoRepository.findByActivoTrue().stream()
                .filter(v -> (v.getProximoServiceFecha() != null && !v.getProximoServiceFecha().isAfter(limite))
                        || (v.getProximoCambioAceiteFecha() != null && !v.getProximoCambioAceiteFecha().isAfter(limite)))
                .map(vehiculoMapper::toResponse)
                .toList();
    }

    private Vehiculo obtenerVehiculo(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No se encontró el vehículo con id " + id));
    }

    private void registrarKilometraje(Vehiculo vehiculo, Integer kilometraje, String observacion) {
        KilometrajeHistorial historial = KilometrajeHistorial.builder()
                .vehiculo(vehiculo)
                .kilometraje(kilometraje)
                .observacion(observacion)
                .build();
        kilometrajeHistorialRepository.save(historial);
    }
}