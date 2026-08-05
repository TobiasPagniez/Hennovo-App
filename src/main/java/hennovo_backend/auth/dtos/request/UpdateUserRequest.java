package hennovo_backend.auth.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(

        @NotBlank
        String nombre,
        
        @NotBlank
        String apellido

) {
}