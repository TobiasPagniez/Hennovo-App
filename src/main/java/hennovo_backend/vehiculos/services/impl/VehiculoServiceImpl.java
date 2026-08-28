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

    private static final int UMBRAL_KM_PROXIMO = 1000;

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
    public List<VehiculoResponse> listarTodos() {
        return vehiculoRepository.findAll().stream()
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
        if (request.getVencimientoSeguro() != null) vehiculo.setVencimientoSeguro(request.getVencimientoSeguro());
        if (request.getVencimientoItv() != null) vehiculo.setVencimientoItv(request.getVencimientoItv());
        if (request.getVencimientoSenasa() != null) vehiculo.setVencimientoSenasa(request.getVencimientoSenasa());

        if (request.getProximoCambioAceiteKm() != null) vehiculo.setProximoCambioAceiteKm(request.getProximoCambioAceiteKm());
        if (request.getProximaRotacionAlineadoKm() != null) vehiculo.setProximaRotacionAlineadoKm(request.getProximaRotacionAlineadoKm());
        if (request.getProximoCambioCorreaKm() != null) vehiculo.setProximoCambioCorreaKm(request.getProximoCambioCorreaKm());

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
    public VehiculoResponse reactivar(Long id) {
        Vehiculo vehiculo = obtenerVehiculo(id);
        vehiculo.setActivo(true);
        return vehiculoMapper.toResponse(vehiculoRepository.save(vehiculo));
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
        obtenerVehiculo(id);
        return kilometrajeHistorialRepository.findByVehiculo_IdOrderByFechaDesc(id).stream()
                .map(vehiculoMapper::toResponse)
                .toList();
    }

    @Override
    public List<VehiculoResponse> consultarVencimientos(int diasAnticipacion) {
        LocalDate limiteFecha = LocalDate.now().plusDays(diasAnticipacion);

        return vehiculoRepository.findByActivoTrue().stream()
                .filter(v -> tieneVencimientoProximo(v, limiteFecha))
                .map(vehiculoMapper::toResponse)
                .toList();
    }

    private boolean tieneVencimientoProximo(Vehiculo v, LocalDate limiteFecha) {

        boolean fechaProxima =
                fechaDentroDelLimite(v.getProximoServiceFecha(), limiteFecha)
                        || fechaDentroDelLimite(v.getVencimientoSeguro(), limiteFecha)
                        || fechaDentroDelLimite(v.getVencimientoItv(), limiteFecha)
                        || fechaDentroDelLimite(v.getVencimientoSenasa(), limiteFecha);

        boolean kmProximo =
                kmDentroDelUmbral(v.getKilometrajeActual(), v.getProximoCambioAceiteKm())
                        || kmDentroDelUmbral(v.getKilometrajeActual(), v.getProximaRotacionAlineadoKm())
                        || kmDentroDelUmbral(v.getKilometrajeActual(), v.getProximoCambioCorreaKm());

        return fechaProxima || kmProximo;
    }

    private boolean fechaDentroDelLimite(LocalDate fecha, LocalDate limite) {
        return fecha != null && !fecha.isAfter(limite);
    }

    private boolean kmDentroDelUmbral(Integer kmActual, Integer kmObjetivo) {
        return kmObjetivo != null && (kmObjetivo - kmActual) <= UMBRAL_KM_PROXIMO;
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
