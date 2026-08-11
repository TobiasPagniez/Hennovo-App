package hennovo_backend.rutas.services.interfaces;

import java.util.List;

import hennovo_backend.rutas.dtos.request.RutaRequest;
import hennovo_backend.rutas.dtos.response.RutaResponse;

public interface RutaService {

    RutaResponse crear(RutaRequest request);

    RutaResponse obtenerPorId(Long id);

    List<RutaResponse> listar();

    RutaResponse actualizar(Long id, RutaRequest request);

    void desactivar(Long id);
}
