package hennovo_backend.productos.services;

import hennovo_backend.productos.dtos.ProductoRequest;
import hennovo_backend.productos.dtos.ProductoResponse;

import java.util.List;

public interface ProductoService {

    ProductoResponse crear(ProductoRequest request);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    void desactivar(Long id);

    ProductoResponse obtenerPorId(Long id);

    List<ProductoResponse> listarActivos();
}
