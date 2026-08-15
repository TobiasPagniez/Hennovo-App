package hennovo_backend.remitos.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RemitoResponse(

        Long id,

        LocalDate fecha,

        Long pedidoId,

        Long clienteId,

        String clienteNombre,

        String clienteDireccion,

        String clienteLocalidad,

        Boolean correspondeFacturacion,

        List<DetalleRemitoResponse> detalles,

        BigDecimal total

) {
}
