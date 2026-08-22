package hennovo_backend.rutas.dtos.response;

public record RutaPedidoResponse(

        Long pedidoId,

        Integer orden,

        Long clienteId,

        String clienteNombre,

        String direccion,

        Boolean entregado

) {
}
