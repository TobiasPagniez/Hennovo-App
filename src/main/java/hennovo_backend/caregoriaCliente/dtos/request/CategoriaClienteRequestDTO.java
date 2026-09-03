package hennovo_backend.caregoriaCliente.dtos.request;

import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.constraints.NotBlank;

public record CategoriaClienteRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String nombre

) {
}
