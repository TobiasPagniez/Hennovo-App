package hennovo_backend.pedidoshabituales.services.impl;

import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.clientes.repositorys.ClienteRepository;
import hennovo_backend.pedidoshabituales.dtos.request.PedidoHabitualRequestDTO;
import hennovo_backend.pedidoshabituales.dtos.response.PedidoHabitualResponseDTO;
import hennovo_backend.pedidoshabituales.entitys.PedidoHabitual;
import hennovo_backend.pedidoshabituales.exceptions.PedidoHabitualDuplicadoException;
import hennovo_backend.pedidoshabituales.exceptions.PedidoHabitualNoEncontradoException;
import hennovo_backend.pedidoshabituales.mapper.PedidoHabitualMapper;
import hennovo_backend.pedidoshabituales.repository.PedidoHabitualRepository;
import hennovo_backend.pedidoshabituales.services.interfaces.PedidoHabitualService;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.productos.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoHabitualServiceImpl implements PedidoHabitualService {

    private final PedidoHabitualRepository pedidoHabitualRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final PedidoHabitualMapper pedidoHabitualMapper;

    @Override
    public PedidoHabitualResponseDTO crear(PedidoHabitualRequestDTO dto) {

        if (pedidoHabitualRepository.existsByClienteIdAndProductoId(
                dto.idCliente(),
                dto.idProducto())) {

            throw new PedidoHabitualDuplicadoException();
        }

        Cliente cliente = clienteRepository.findById(dto.idCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        Producto producto = productoRepository.findById(dto.idProducto())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        if (!producto.getActivo()) {
            throw new EntityNotFoundException("No se puede agregar un producto inactivo");
        }

        PedidoHabitual pedidoHabitual = new PedidoHabitual();

        pedidoHabitual.setCliente(cliente);
        pedidoHabitual.setProducto(producto);
        pedidoHabitual.setCantidad(dto.cantidad());

        return pedidoHabitualMapper.toResponseDTO(pedidoHabitualRepository.save(pedidoHabitual));
    }

    @Override
    public PedidoHabitualResponseDTO obtenerPorId(Long id) {

        PedidoHabitual pedidoHabitual = pedidoHabitualRepository.findById(id)
                        .orElseThrow(() -> new PedidoHabitualNoEncontradoException(id));

        return pedidoHabitualMapper.toResponseDTO(pedidoHabitual);
    }

    @Override
    public List<PedidoHabitualResponseDTO> obtenerPorCliente(Long idCliente) {

        clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        return pedidoHabitualRepository.findByClienteId(idCliente)
                .stream()
                .map(pedidoHabitualMapper::toResponseDTO)
                .toList();
    }

    @Override
    public PedidoHabitualResponseDTO modificar(Long id, PedidoHabitualRequestDTO dto) {

        PedidoHabitual pedidoHabitual = pedidoHabitualRepository.findById(id)
                        .orElseThrow(() -> new PedidoHabitualNoEncontradoException(id));

        if (!pedidoHabitual.getCliente().getId()
                .equals(dto.idCliente())
                || !pedidoHabitual.getProducto().getId()
                .equals(dto.idProducto())) {

            if (pedidoHabitualRepository
                    .existsByClienteIdAndProductoId(
                            dto.idCliente(),
                            dto.idProducto())) {

                throw new PedidoHabitualDuplicadoException();
            }
        }

        Cliente cliente = clienteRepository.findById(dto.idCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        Producto producto = productoRepository.findById(dto.idProducto())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        if (!producto.getActivo()) {
            throw new EntityNotFoundException("No se puede asociar un producto inactivo");
        }

        pedidoHabitual.setCliente(cliente);
        pedidoHabitual.setProducto(producto);
        pedidoHabitual.setCantidad(dto.cantidad());

        return pedidoHabitualMapper.toResponseDTO(pedidoHabitualRepository.save(pedidoHabitual));
    }

    @Override
    public void eliminar(Long id) {

        PedidoHabitual pedidoHabitual =
                pedidoHabitualRepository.findById(id)
                        .orElseThrow(() -> new PedidoHabitualNoEncontradoException(id));

        pedidoHabitualRepository.delete(pedidoHabitual);
    }
}
