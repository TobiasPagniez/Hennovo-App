package hennovo_backend.vehiculos.mapper;

import hennovo_backend.vehiculos.dtos.request.CreateVehiculoRequest;
import hennovo_backend.vehiculos.dtos.response.KilometrajeHistorialResponse;
import hennovo_backend.vehiculos.dtos.response.VehiculoResponse;
import hennovo_backend.vehiculos.entitys.KilometrajeHistorial;
import hennovo_backend.vehiculos.entitys.Vehiculo;
import org.springframework.stereotype.Component;

@Component
public class VehiculoMapper {

    public Vehiculo toEntity(CreateVehiculoRequest request) {
        return Vehiculo.builder()
                .patente(request.getPatente())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .anio(request.getAnio())
                .kilometrajeActual(request.getKilometrajeActual())
                .activo(true)
                .build();
    }

    public VehiculoResponse toResponse(Vehiculo vehiculo) {
        return VehiculoResponse.builder()
                .id(vehiculo.getId())
                .patente(vehiculo.getPatente())
                .marca(vehiculo.getMarca())
                .modelo(vehiculo.getModelo())
                .anio(vehiculo.getAnio())
                .kilometrajeActual(vehiculo.getKilometrajeActual())
                .proximoServiceFecha(vehiculo.getProximoServiceFecha())
                .proximoCambioAceiteFecha(vehiculo.getProximoCambioAceiteFecha())
                .observacionesMantenimiento(vehiculo.getObservacionesMantenimiento())
                .activo(vehiculo.getActivo())
                .build();
    }

    public KilometrajeHistorialResponse toResponse(KilometrajeHistorial historial) {
        return KilometrajeHistorialResponse.builder()
                .id(historial.getId())
                .vehiculoId(historial.getVehiculo().getId())
                .kilometraje(historial.getKilometraje())
                .fecha(historial.getFecha())
                .observacion(historial.getObservacion())
                .build();
    }
}