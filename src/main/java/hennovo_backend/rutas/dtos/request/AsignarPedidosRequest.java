package hennovo_backend.rutas.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record AsignarPedidosRequest(

        @NotEmpty(message = "Debe indicar al menos un pedido")
        List<@Valid OrdenPedidoRequest> pedidos

) {
}