package hennovo_backend.pedidoshabituales.services.interfaces;

import hennovo_backend.pedidoshabituales.dtos.request.PedidoHabitualRequestDTO;
import hennovo_backend.pedidoshabituales.dtos.response.PedidoHabitualResponseDTO;

import java.util.List;

public interface PedidoHabitualService {

    PedidoHabitualResponseDTO crear(PedidoHabitualRequestDTO dto);

    PedidoHabitualResponseDTO obtenerPorId(Long id);

    List<PedidoHabitualResponseDTO> obtenerPorCliente(Long idCliente);

    PedidoHabitualResponseDTO modificar(Long id, PedidoHabitualRequestDTO dto);

    void eliminar(Long id);
}