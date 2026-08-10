package hennovo_backend.pedidos.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import hennovo_backend.pedidos.dtos.request.DetallePedidoRequest;
import hennovo_backend.pedidos.dtos.response.DetallePedidoResponse;
import hennovo_backend.pedidos.entitys.DetallePedido;
import hennovo_backend.productos.entity.Producto;

@Component
public class DetallePedidoMapper {

    public DetallePedido toEntity(
            DetallePedidoRequest request,
            Producto producto
    ) {
        DetallePedido detalle = new DetallePedido();

        detalle.setProducto(producto);
        detalle.setCantidad(request.cantidad());
        detalle.setUnidad(request.unidad());

        return detalle;
    }

    public DetallePedidoResponse toResponse(DetallePedido detalle) {

        BigDecimal subtotal = detalle.getPrecioUnitario()
                .multiply(
                        BigDecimal.valueOf(detalle.getCantidad())
                );

        return new DetallePedidoResponse(
                detalle.getId(),
                detalle.getProducto().getId(),
                detalle.getCantidad(),
                detalle.getUnidad(),
                detalle.getPrecioUnitario(),
                subtotal
        );
    }
}