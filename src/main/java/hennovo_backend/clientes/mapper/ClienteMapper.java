package hennovo_backend.clientes.mapper;

import org.springframework.stereotype.Component;

import hennovo_backend.clientes.dtos.request.ClienteRequestDTO;
import hennovo_backend.clientes.dtos.response.ClienteResponseDTO;
import hennovo_backend.clientes.entitys.Cliente;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();

        cliente.setNombre(dto.nombre());
        cliente.setDireccion(dto.direccion());
        cliente.setLocalidad(dto.localidad());
        cliente.setTelefono(dto.telefono());

        return cliente;
    }

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getDireccion(),
                cliente.getLocalidad(),
                cliente.getTelefono(),
                cliente.getActivo(),
                cliente.getCategoria().getId(),
                cliente.getCategoria().getNombre()
        );
    }
}
