package hennovo_backend.plantillacarga.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import hennovo_backend.plantillacarga.dtos.request.ActualizarCantidadDetalleRequest;
import hennovo_backend.plantillacarga.dtos.request.AgregarDetalleCeldaRequest;
import hennovo_backend.plantillacarga.dtos.request.ConfigurarCroquisRequest;
import hennovo_backend.plantillacarga.dtos.request.CopiarCroquisRequest;
import hennovo_backend.plantillacarga.dtos.request.MoverDetalleCeldaRequest;
import hennovo_backend.plantillacarga.dtos.response.PlantillaCargaResponse;

public interface PlantillaCargaService {

    List<PlantillaCargaResponse> obtenerPorVehiculo(Long vehiculoId, LocalDate fecha);

    List<PlantillaCargaResponse> configurar(Long vehiculoId, ConfigurarCroquisRequest request);

    void agregarDetalle(Long celdaId, AgregarDetalleCeldaRequest request);

    void moverDetalle(Long detalleId, MoverDetalleCeldaRequest request);

    void actualizarCantidad(Long detalleId, ActualizarCantidadDetalleRequest request);

    void eliminarDetalle(Long detalleId);

    void copiarDia(Long vehiculoId, CopiarCroquisRequest request);

    List<LocalDate> obtenerFechasConContenido(Long vehiculoId);
}
