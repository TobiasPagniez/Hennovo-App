package hennovo_backend.productos.services;

import hennovo_backend.productos.dtos.ProductoRequest;
import hennovo_backend.productos.dtos.ProductoResponse;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.productos.mapper.ProductoMapper;
import hennovo_backend.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public ProductoResponse crear(ProductoRequest request) {

        if (productoRepository.existsByTipoHuevoAndTamanoAndPresentacion(
                request.getTipoHuevo(),
                request.getTamaño(),
                request.getPresentacion())) {

            throw new RuntimeException("El producto ya existe.");
        }

        Producto producto = productoMapper.toEntity(request);

        producto.setActivo(true);

        return productoMapper.toResponse(
                productoRepository.save(producto)
        );
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (productoRepository.existsByTipoHuevoAndTamanoAndPresentacion(
                request.getTipoHuevo(),
                request.getTamaño(),
                request.getPresentacion())
                &&
                (!producto.getTipoHuevo().equals(request.getTipoHuevo())
                        || !producto.getTamaño().equals(request.getTamaño())
                        || !producto.getPresentacion().equals(request.getPresentacion()))) {

            throw new RuntimeException("Ya existe un producto con esos datos.");
        }

        producto.setTipoHuevo(request.getTipoHuevo());
        producto.setTamaño(request.getTamaño());
        producto.setPresentacion(request.getPresentacion());

        return productoMapper.toResponse(
                productoRepository.save(producto)
        );
    }

    @Override
    public void desactivar(Long id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setActivo(false);

        productoRepository.save(producto);
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return productoMapper.toResponse(producto);
    }

    @Override
    public List<ProductoResponse> listarActivos() {

        return productoRepository.findByActivoTrue()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }
}
