package hennovo_backend.perdidas.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import hennovo_backend.perdidas.dtos.request.PerdidaRequest;
import hennovo_backend.perdidas.dtos.response.PerdidaResponse;

public interface PerdidaService {

    PerdidaResponse crear(PerdidaRequest request);

    PerdidaResponse obtenerPorId(Long id);

    List<PerdidaResponse> listarTodos();

    List<PerdidaResponse> listarPorRangoFechas(LocalDate desde, LocalDate hasta);

    PerdidaResponse actualizar(Long id, PerdidaRequest request);

    void eliminar(Long id);
}
