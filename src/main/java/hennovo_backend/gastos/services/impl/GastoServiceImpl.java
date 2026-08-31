package hennovo_backend.gastos.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.gastos.dtos.request.GastoRequest;
import hennovo_backend.gastos.dtos.response.GastoResponse;
import hennovo_backend.gastos.entitys.Gasto;
import hennovo_backend.gastos.mapper.GastoMapper;
import hennovo_backend.gastos.repositorys.GastoRepository;
import hennovo_backend.gastos.services.interfaces.GastoService;
import hennovo_backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final GastoMapper gastoMapper;

    @Override
    @Transactional
    public GastoResponse crear(GastoRequest request) {

        Gasto gasto = gastoMapper.toEntity(request);

        return gastoMapper.toResponse(gastoRepository.save(gasto));
    }

    @Override
    @Transactional(readOnly = true)
    public GastoResponse obtenerPorId(Long id) {

        return gastoMapper.toResponse(obtenerGasto(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponse> listarTodos() {

        return gastoRepository.findAll()
                .stream()
                .map(gastoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponse> listarPorRangoFechas(LocalDate desde, LocalDate hasta) {

        return gastoRepository.findByFechaBetween(desde, hasta)
                .stream()
                .map(gastoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public GastoResponse actualizar(Long id, GastoRequest request) {

        Gasto gasto = obtenerGasto(id);

        gastoMapper.actualizarEntity(gasto, request);

        return gastoMapper.toResponse(gastoRepository.save(gasto));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        Gasto gasto = obtenerGasto(id);

        gastoRepository.delete(gasto);
    }

    private Gasto obtenerGasto(Long id) {
        return gastoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Gasto no encontrado"));
    }
}
