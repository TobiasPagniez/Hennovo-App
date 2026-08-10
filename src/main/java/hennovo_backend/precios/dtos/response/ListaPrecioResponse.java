package hennovo_backend.precios.dtos.response;

import java.time.LocalDate;
import java.util.List;

public record ListaPrecioResponse(
        Long id,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        List<PrecioProductoResponse> precios
) {
}
