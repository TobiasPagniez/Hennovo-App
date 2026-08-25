package hennovo_backend.pedidos.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PedidoResponse(

                Long id,

                LocalDate fecha,

                Boolean entregado,

                Boolean pagado,

                String observaciones,

                Long clienteId,

                String clienteNombre,

                Long usuarioId,

                Long rutaId,

                Integer ordenRuta,

                List<DetallePedidoResponse> detalles,

                BigDecimal total) {
}
