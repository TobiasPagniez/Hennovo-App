package hennovo_backend.pedidos.dtos.request;

import jakarta.validation.constraints.NotNull;

public record AsignarUsuarioPedidoRequest(
    @NotNull(message = "El usuario es obligatorio")
    Long usuarioId
) {}
