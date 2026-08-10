package hennovo_backend.caregoriaCliente.mapper;

import org.springframework.stereotype.Component;

import hennovo_backend.caregoriaCliente.dtos.request.CategoriaClienteRequestDTO;
import hennovo_backend.caregoriaCliente.dtos.response.CategoriaClienteResponseDTO;
import hennovo_backend.clientes.entitys.CategoriaCliente;

@Component
public class CategoriaClienteMapper {

    public CategoriaCliente toEntity(CategoriaClienteRequestDTO dto) {

        CategoriaCliente categoria = new CategoriaCliente();

        categoria.setNombre(dto.nombre());

        return categoria;
    }

    public CategoriaClienteResponseDTO toResponseDTO(
            CategoriaCliente categoria) {

        return new CategoriaClienteResponseDTO(
                categoria.getId(),
                categoria.getNombre()
        );
    }
}
