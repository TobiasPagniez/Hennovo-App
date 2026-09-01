package hennovo_backend.perdidas.mapper;

import org.springframework.stereotype.Component;

import hennovo_backend.perdidas.dtos.request.PerdidaRequest;
import hennovo_backend.perdidas.dtos.response.PerdidaResponse;
import hennovo_backend.perdidas.entitys.Perdida;
import hennovo_backend.productos.entity.Producto;

@Component
public class PerdidaMapper {

    public Perdida toEntity(PerdidaRequest request, Producto producto) {

        Perdida perdida = new Perdida();

        perdida.setFecha(request.fecha());
        perdida.setProducto(producto);
        perdida.setCantidad(request.cantidad());
        perdida.setMotivo(request.motivo());
        perdida.setObservaciones(request.observaciones());

        return perdida;
    }

    public void actualizarEntity(Perdida perdida, PerdidaRequest request, Producto producto) {

        perdida.setFecha(request.fecha());
        perdida.setProducto(producto);
        perdida.setCantidad(request.cantidad());
        perdida.setMotivo(request.motivo());
        perdida.setObservaciones(request.observaciones());
    }

    public PerdidaResponse toResponse(Perdida perdida) {

        return new PerdidaResponse(
                perdida.getId(),
                perdida.getFecha(),
                perdida.getProducto().getId(),
                nombreProducto(perdida.getProducto()),
                perdida.getCantidad(),
                perdida.getMotivo(),
                perdida.getObservaciones()
        );
    }

    private String nombreProducto(Producto producto) {
        return producto.getPresentacion() + " " + producto.getTipoHuevo() + " " + producto.getTamaño();
    }
}
