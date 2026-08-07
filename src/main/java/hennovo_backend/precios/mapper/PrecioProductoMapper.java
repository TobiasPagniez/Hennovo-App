package hennovo_backend.precios.mapper;

import org.springframework.stereotype.Component;

import hennovo_backend.precios.dtos.response.PrecioProductoResponse;
import hennovo_backend.precios.entitys.PrecioProducto;

@Component
public class PrecioProductoMapper {
    public PrecioProductoResponse toResponse(PrecioProducto precioProducto) {
        return new PrecioProductoResponse(
                precioProducto.getId(),
                precioProducto.getProducto().getId(),
                precioProducto.getCategoria().getId(),
                precioProducto.getPrecio(),
                precioProducto.getUnidadPrecio()
        );
    }
}
