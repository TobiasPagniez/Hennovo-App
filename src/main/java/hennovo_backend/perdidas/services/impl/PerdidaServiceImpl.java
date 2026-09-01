package hennovo_backend.perdidas.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.perdidas.dtos.request.PerdidaRequest;
import hennovo_backend.perdidas.dtos.response.PerdidaResponse;
import hennovo_backend.perdidas.entitys.Perdida;
import hennovo_backend.perdidas.mapper.PerdidaMapper;
import hennovo_backend.perdidas.repositorys.PerdidaRepository;
import hennovo_backend.perdidas.services.interfaces.PerdidaService;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.productos.repository.ProductoRepository;
import hennovo_backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerdidaServiceImpl implements PerdidaService {

    private final PerdidaRepository perdidaRepository;
    private final ProductoRepository productoRepository;
    private final PerdidaMapper perdidaMapper;

    @Override
    @Transactional
    public PerdidaResponse crear(PerdidaRequest request) {

        Producto producto = obtenerProducto(request.productoId());

        Perdida perdida = perdidaMapper.toEntity(request, producto);

        return perdidaMapper.toResponse(perdidaRepository.save(perdida));
    }

    @Override
    @Transactional(readOnly = true)
    public PerdidaResponse obtenerPorId(Long id) {

        return perdidaMapper.toResponse(obtenerPerdida(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerdidaResponse> listarTodos() {

        return perdidaRepository.findAll()
                .stream()
                .map(perdidaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerdidaResponse> listarPorRangoFechas(LocalDate desde, LocalDate hasta) {

        return perdidaRepository.findByFechaBetween(desde, hasta)
                .stream()
                .map(perdidaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PerdidaResponse actualizar(Long id, PerdidaRequest request) {

        Perdida perdida = obtenerPerdida(id);
        Producto producto = obtenerProducto(request.productoId());

        perdidaMapper.actualizarEntity(perdida, request, producto);

        return perdidaMapper.toResponse(perdidaRepository.save(perdida));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        Perdida perdida = obtenerPerdida(id);

        perdidaRepository.delete(perdida);
    }

    private Perdida obtenerPerdida(Long id) {
        return perdidaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pérdida no encontrada"));
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }
}
