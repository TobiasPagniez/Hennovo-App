package hennovo_backend.pagos.services.interfaces;

import java.util.List;

import hennovo_backend.pagos.dtos.request.PagoRequestDTO;
import hennovo_backend.pagos.dtos.response.PagoResponseDTO;

public interface PagoService {

    PagoResponseDTO crear(PagoRequestDTO dto);

    PagoResponseDTO obtenerPorId(Long id);

    List<PagoResponseDTO> obtenerTodos();

    List<PagoResponseDTO> obtenerPorCliente(Long clienteId);

    void anular(Long id);
}
