package hennovo_backend.auth.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
        String newPassword

) {
}
