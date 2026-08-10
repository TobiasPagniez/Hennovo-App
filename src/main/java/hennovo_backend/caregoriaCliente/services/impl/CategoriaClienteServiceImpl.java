package hennovo_backend.caregoriaCliente.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import hennovo_backend.caregoriaCliente.dtos.request.CategoriaClienteRequestDTO;
import hennovo_backend.caregoriaCliente.dtos.response.CategoriaClienteResponseDTO;
import hennovo_backend.clientes.entitys.CategoriaCliente;
import hennovo_backend.clientes.repositorys.CategoriaClienteRepository;
import hennovo_backend.caregoriaCliente.mapper.CategoriaClienteMapper;

import hennovo_backend.caregoriaCliente.services.interfaces.CategoriaClienteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaClienteServiceImpl implements CategoriaClienteService {

    private final CategoriaClienteRepository categoriaClienteRepository;
    private final CategoriaClienteMapper categoriaClienteMapper;

    @Override
    public CategoriaClienteResponseDTO crear(
            CategoriaClienteRequestDTO dto) {

        CategoriaCliente categoria = categoriaClienteMapper.toEntity(dto);

        CategoriaCliente categoriaGuardada =
                categoriaClienteRepository.save(categoria);

        return categoriaClienteMapper.toResponseDTO(categoriaGuardada);
    }

    @Override
    public CategoriaClienteResponseDTO obtenerPorId(Long id) {

        CategoriaCliente categoria =
                categoriaClienteRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Categoría no encontrada"
                                )
                        );

        return categoriaClienteMapper.toResponseDTO(categoria);
    }

    @Override
    public List<CategoriaClienteResponseDTO> obtenerTodos() {

        return categoriaClienteRepository.findAll()
                .stream()
                .map(categoriaClienteMapper::toResponseDTO)
                .toList();
    }

    @Override
    public CategoriaClienteResponseDTO modificar(
            Long id,
            CategoriaClienteRequestDTO dto) {

        CategoriaCliente categoria =
                categoriaClienteRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Categoría no encontrada"
                                )
                        );

        categoria.setNombre(dto.nombre());

        CategoriaCliente categoriaActualizada =
                categoriaClienteRepository.save(categoria);

        return categoriaClienteMapper.toResponseDTO(
                categoriaActualizada
        );
    }

    @Override
    public void eliminar(Long id) {

        CategoriaCliente categoria =
                categoriaClienteRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Categoría no encontrada"
                                )
                        );

        categoriaClienteRepository.delete(categoria);
    }
}
