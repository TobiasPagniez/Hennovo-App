package hennovo_backend.vehiculos.services.interfaces;

import hennovo_backend.vehiculos.dtos.request.ActualizarKilometrajeRequest;
import hennovo_backend.vehiculos.dtos.request.CreateVehiculoRequest;
import hennovo_backend.vehiculos.dtos.request.UpdateVehiculoRequest;
import hennovo_backend.vehiculos.dtos.response.KilometrajeHistorialResponse;
import hennovo_backend.vehiculos.dtos.response.VehiculoResponse;

import java.util.List;

public interface VehiculoService {

    VehiculoResponse crear(CreateVehiculoRequest request);

    List<VehiculoResponse> listar();

    List<VehiculoResponse> listarTodos();

    VehiculoResponse buscarPorId(Long id);

    VehiculoResponse actualizar(Long id, UpdateVehiculoRequest request);

    void desactivar(Long id);

    VehiculoResponse reactivar(Long id);

    VehiculoResponse actualizarKilometraje(Long id, ActualizarKilometrajeRequest request);

    List<KilometrajeHistorialResponse> historialKilometraje(Long id);

    List<VehiculoResponse> consultarVencimientos(int diasAnticipacion);
}
