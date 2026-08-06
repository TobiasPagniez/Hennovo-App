package hennovo_backend.productos.mapper;

import hennovo_backend.productos.dtos.ProductoRequest;
import hennovo_backend.productos.dtos.ProductoResponse;
import hennovo_backend.productos.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequest request) {

        Producto producto = new Producto();

        producto.setTipoHuevo(request.getTipoHuevo());
        producto.setTamaño(request.getTamaño());
        producto.setPresentacion(request.getPresentacion());

        return producto;
    }

    public ProductoResponse toResponse(Producto producto) {

        ProductoResponse response = new ProductoResponse();

        response.setId(producto.getId());
        response.setTipoHuevo(producto.getTipoHuevo());
        response.setTamaño(producto.getTamaño());
        response.setPresentacion(producto.getPresentacion());
        response.setActivo(producto.getActivo());

        return response;
    }
}