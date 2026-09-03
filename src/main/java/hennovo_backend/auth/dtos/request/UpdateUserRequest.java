package hennovo_backend.auth.dtos.request;

import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(

        @NotBlank
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String nombre,
        
        @NotBlank
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String apellido

) {
}