package hennovo_backend.clientes.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import hennovo_backend.clientes.dtos.request.ClienteRequestDTO;
import hennovo_backend.clientes.dtos.response.ClienteResponseDTO;
import hennovo_backend.clientes.entitys.CategoriaCliente;
import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.clientes.mapper.ClienteMapper;
import hennovo_backend.clientes.repositorys.CategoriaClienteRepository;
import hennovo_backend.clientes.repositorys.ClienteRepository;
import hennovo_backend.clientes.services.interfaces.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final CategoriaClienteRepository categoriaClienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public ClienteResponseDTO crear(ClienteRequestDTO dto) {

        CategoriaCliente categoria = categoriaClienteRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        Cliente cliente = clienteMapper.toEntity(dto);

        cliente.setCategoria(categoria);
        cliente.setActivo(true);

        Cliente clienteGuardado = clienteRepository.save(cliente);

        return clienteMapper.toResponseDTO(clienteGuardado);
    }

    @Override
    public ClienteResponseDTO obtenerPorId(Long id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        return clienteMapper.toResponseDTO(cliente);
    }

    @Override
    public List<ClienteResponseDTO> obtenerTodos() {

        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ClienteResponseDTO modificar(Long id, ClienteRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        CategoriaCliente categoria = categoriaClienteRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        cliente.setNombre(dto.nombre());
        cliente.setDireccion(dto.direccion());
        cliente.setLocalidad(dto.localidad());
        cliente.setTelefono(dto.telefono());
        cliente.setCategoria(categoria);

        Cliente clienteActualizado = clienteRepository.save(cliente);

        return clienteMapper.toResponseDTO(clienteActualizado);
    }

    @Override
    public void desactivar(Long id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        cliente.setActivo(false);

        clienteRepository.save(cliente);
    }

    @Override
    public ClienteResponseDTO reactivar(Long id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        cliente.setActivo(true);

        Cliente clienteActualizado = clienteRepository.save(cliente);

        return clienteMapper.toResponseDTO(clienteActualizado);
    }

    @Override
    public List<ClienteResponseDTO> buscarPorNombre(String nombre) {

        return clienteRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(clienteMapper::toResponseDTO)
                .toList();
    }

}
