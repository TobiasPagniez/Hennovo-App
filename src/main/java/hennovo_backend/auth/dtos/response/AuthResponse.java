package hennovo_backend.auth.dtos.response;

import hennovo_backend.auth.entitys.NombreRol;


public record AuthResponse(
        String token,
        String type,
        Long id,
        String nombre,
        String apellido,
        String email,
        NombreRol rol
) {
}