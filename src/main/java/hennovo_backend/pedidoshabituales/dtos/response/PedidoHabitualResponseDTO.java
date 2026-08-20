package hennovo_backend.pedidoshabituales.dtos.response;

public record PedidoHabitualResponseDTO(

        Long id,

        Long idCliente,

        String nombreCliente,

        Long idProducto,

        String producto,

        Integer cantidad

) {
}