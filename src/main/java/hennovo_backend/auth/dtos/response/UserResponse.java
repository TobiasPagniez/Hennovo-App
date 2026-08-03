package hennovo_backend.auth.dtos.response;

import hennovo_backend.auth.entitys.NombreRol;

public record UserResponse(

        Long id,
        String nombre,
        String apellido,
        String email,
        NombreRol rol,
        Boolean activo

) {
}