package hennovo_backend.caregoriaCliente.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record CategoriaClienteRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre

) {
}
