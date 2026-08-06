package hennovo_backend.clientes.services.interfaces;

import java.util.List;

import hennovo_backend.clientes.dtos.request.ClienteRequestDTO;
import hennovo_backend.clientes.dtos.response.ClienteResponseDTO;

public interface ClienteService {

    ClienteResponseDTO crear(ClienteRequestDTO dto);

    ClienteResponseDTO obtenerPorId(Long id);

    List<ClienteResponseDTO> obtenerTodos();

    ClienteResponseDTO modificar(Long id, ClienteRequestDTO dto);

    void desactivar(Long id);
}
