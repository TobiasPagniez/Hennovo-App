package hennovo_backend.precios.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import hennovo_backend.precios.dtos.request.ListaPrecioRequest;
import hennovo_backend.precios.dtos.response.ListaPrecioResponse;
import hennovo_backend.precios.dtos.response.PrecioProductoResponse;
import hennovo_backend.precios.entitys.ListaPrecio;

@Component
public class ListaPrecioMapper {
    public ListaPrecio toEntity(ListaPrecioRequest request) {
        ListaPrecio lista = new ListaPrecio();
        lista.setFechaDesde(request.fechaDesde());
        lista.setFechaHasta(request.fechaHasta());
        return lista;
    }

    public ListaPrecioResponse toResponse(ListaPrecio lista, List<PrecioProductoResponse> precios) {
        return new ListaPrecioResponse(
                lista.getId(),
                lista.getFechaDesde(),
                lista.getFechaHasta(),
                precios
        );
    }
}
