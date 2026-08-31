package hennovo_backend.gastos.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import hennovo_backend.gastos.dtos.request.GastoRequest;
import hennovo_backend.gastos.dtos.response.GastoResponse;

public interface GastoService {

    GastoResponse crear(GastoRequest request);

    GastoResponse obtenerPorId(Long id);

    List<GastoResponse> listarTodos();

    List<GastoResponse> listarPorRangoFechas(LocalDate desde, LocalDate hasta);

    GastoResponse actualizar(Long id, GastoRequest request);

    void eliminar(Long id);
}
