package hennovo_backend.remitos.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.pedidos.entitys.Pedido;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.remitos.dtos.request.DetalleRemitoRequest;
import hennovo_backend.remitos.dtos.request.RemitoRequest;
import hennovo_backend.remitos.dtos.response.DetalleRemitoResponse;
import hennovo_backend.remitos.dtos.response.RemitoResponse;
import hennovo_backend.remitos.entitys.DetalleRemito;
import hennovo_backend.remitos.entitys.Remito;

@Component
public class RemitoMapper {

    public Remito toEntity(
            RemitoRequest request,
            Pedido pedido) {

        Remito remito = new Remito();

        remito.setFecha(request.fecha());
        remito.setCorrespondeFacturacion(
                request.correspondeFacturacion()
        );
        remito.setPedido(pedido);

        return remito;
    }

    public DetalleRemito toDetalleEntity(
            DetalleRemitoRequest request,
            Producto producto) {

        DetalleRemito detalle = new DetalleRemito();

        detalle.setCantidad(request.cantidad());
        detalle.setUnidad(request.unidad());
        detalle.setProducto(producto);

        return detalle;
    }

    public DetalleRemitoResponse toDetalleResponse(
            DetalleRemito detalle) {

        BigDecimal importe =
                detalle.getPrecioUnitario()
                        .multiply(
                                BigDecimal.valueOf(
                                        detalle.getCantidad()
                                )
                        );

        return new DetalleRemitoResponse(
                detalle.getId(),
                detalle.getProducto().getId(),
                detalle.getCantidad(),
                detalle.getUnidad(),
                detalle.getPrecioUnitario(),
                importe
        );
    }

    public RemitoResponse toResponse(
            Remito remito,
            List<DetalleRemitoResponse> detalles,
            BigDecimal total) {

        Pedido pedido = remito.getPedido();
        Cliente cliente = pedido.getCliente();

        return new RemitoResponse(
                remito.getId(),
                remito.getFecha(),
                pedido.getId(),
                cliente.getId(),
                cliente.getNombre(),
                cliente.getDireccion(),
                null,
                remito.getCorrespondeFacturacion(),
                detalles,
                total
        );
    }
}
