package hennovo_backend.precios.services.interfaces;

import java.util.List;

import hennovo_backend.precios.dtos.request.ListaPrecioRequest;
import hennovo_backend.precios.dtos.response.ListaPrecioResponse;

public interface ListaPrecioService {

    ListaPrecioResponse crear(ListaPrecioRequest request);

    ListaPrecioResponse actualizar(Long id, ListaPrecioRequest request);

    ListaPrecioResponse obtenerPorId(Long id);

    List<ListaPrecioResponse> listar();

    ListaPrecioResponse obtenerVigente();
}
