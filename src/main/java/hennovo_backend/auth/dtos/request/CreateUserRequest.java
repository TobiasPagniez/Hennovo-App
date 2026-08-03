package hennovo_backend.auth.dtos.request;

import hennovo_backend.auth.entitys.NombreRol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank
        @Size(max=20)
        String nombre,
        
        @NotBlank
        @Size(max=20)
        String apellido,
        
        @NotBlank
        @Email
        String email,
        
        @NotBlank
        String password,

        NombreRol rol

) {
}