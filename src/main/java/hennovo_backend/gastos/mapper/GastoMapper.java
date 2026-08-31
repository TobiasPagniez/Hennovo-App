package hennovo_backend.gastos.mapper;

import org.springframework.stereotype.Component;

import hennovo_backend.gastos.dtos.request.GastoRequest;
import hennovo_backend.gastos.dtos.response.GastoResponse;
import hennovo_backend.gastos.entitys.Gasto;

@Component
public class GastoMapper {

    public Gasto toEntity(GastoRequest request) {

        Gasto gasto = new Gasto();

        gasto.setFecha(request.fecha());
        gasto.setCategoria(request.categoria());
        gasto.setDescripcion(request.descripcion());
        gasto.setImporte(request.importe());
        gasto.setObservaciones(request.observaciones());

        return gasto;
    }

    public void actualizarEntity(Gasto gasto, GastoRequest request) {

        gasto.setFecha(request.fecha());
        gasto.setCategoria(request.categoria());
        gasto.setDescripcion(request.descripcion());
        gasto.setImporte(request.importe());
        gasto.setObservaciones(request.observaciones());
    }

    public GastoResponse toResponse(Gasto gasto) {

        return new GastoResponse(
                gasto.getId(),
                gasto.getFecha(),
                gasto.getCategoria(),
                gasto.getDescripcion(),
                gasto.getImporte(),
                gasto.getObservaciones()
        );
    }
}
