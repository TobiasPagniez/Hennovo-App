package hennovo_backend.caregoriaCliente.services.interfaces;

import java.util.List;

import hennovo_backend.caregoriaCliente.dtos.request.CategoriaClienteRequestDTO;
import hennovo_backend.caregoriaCliente.dtos.response.CategoriaClienteResponseDTO;

public interface CategoriaClienteService {

    CategoriaClienteResponseDTO crear(CategoriaClienteRequestDTO dto);

    CategoriaClienteResponseDTO obtenerPorId(Long id);

    List<CategoriaClienteResponseDTO> obtenerTodos();

    CategoriaClienteResponseDTO modificar(Long id, CategoriaClienteRequestDTO dto);

    void eliminar(Long id);
}
