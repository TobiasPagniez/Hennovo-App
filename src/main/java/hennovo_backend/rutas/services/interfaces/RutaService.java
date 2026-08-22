package hennovo_backend.rutas.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import hennovo_backend.rutas.dtos.request.AsignarPedidosRequest;
import hennovo_backend.rutas.dtos.request.RutaRequest;
import hennovo_backend.rutas.dtos.response.RutaPedidoResponse;
import hennovo_backend.rutas.dtos.response.RutaResponse;

public interface RutaService {

    RutaResponse crear(RutaRequest request);

    RutaResponse obtenerPorId(Long id);

    List<RutaResponse> listar();

    RutaResponse actualizar(Long id, RutaRequest request);

    void desactivar(Long id);

    List<RutaPedidoResponse> listarPedidos(Long rutaId);

    List<RutaPedidoResponse> asignarPedidos(
            Long rutaId,
            AsignarPedidosRequest request
    );

    void quitarPedido(Long rutaId, Long pedidoId);
    
    List<RutaPedidoResponse> listarPedidosDisponibles(LocalDate fecha);
}
