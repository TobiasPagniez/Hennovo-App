package hennovo_backend.remitos.services.interfaces;

import java.util.List;

import hennovo_backend.remitos.dtos.request.RemitoRequest;
import hennovo_backend.remitos.dtos.response.RemitoResponse;

public interface RemitoService {

    RemitoResponse crear(RemitoRequest request);

    RemitoResponse obtenerPorId(Long id);

    List<RemitoResponse> listar();

    RemitoResponse obtenerPorPedido(Long pedidoId);
}
