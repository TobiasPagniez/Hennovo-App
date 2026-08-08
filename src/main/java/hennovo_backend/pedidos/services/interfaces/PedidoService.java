package hennovo_backend.pedidos.services.interfaces;

import java.util.List;

import hennovo_backend.pedidos.dtos.request.PedidoRequest;
import hennovo_backend.pedidos.dtos.response.PedidoResponse;

public interface PedidoService {

    PedidoResponse crear(PedidoRequest request);

    PedidoResponse obtenerPorId(Long id);

    List<PedidoResponse> listar();

    PedidoResponse actualizar(Long id, PedidoRequest request);

    void marcarComoEntregado(Long id);

    void marcarComoPagado(Long id);
}
