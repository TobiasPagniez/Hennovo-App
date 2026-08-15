package hennovo_backend.remitos.dtos.request;

import java.time.LocalDate;
import java.util.List;

public record RemitoRequest(

        Long pedidoId,

        LocalDate fecha,

        Boolean correspondeFacturacion,

        List<DetalleRemitoRequest> detalles

) {
}
