package hennovo_backend.auth.dtos.request;


import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank
        @Size(max=20)
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String nombre,
        
        @NotBlank
        @Size(max=20)
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String apellido,
        
        @NotBlank
        @Email
        String email,
        
        @NotBlank
        String password



) {
}
