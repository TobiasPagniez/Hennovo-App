package hennovo_backend.clientes.dtos.response;

public record ClienteResponseDTO(

        Long id,

        String nombre,

        String direccion,

        String localidad,

        String telefono,

        Boolean activo,

        Long idCategoria,

        String nombreCategoria

) {
}
