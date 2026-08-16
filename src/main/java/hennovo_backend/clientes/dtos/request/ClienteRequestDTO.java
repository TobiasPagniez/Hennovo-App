package hennovo_backend.clientes.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La dirección es obligatoria")
        String direccion,

        @NotBlank(message = "La localidad es obligatoria")
        String localidad,

        @NotBlank(message = "El teléfono es obligatorio")
        String telefono,

        @NotNull(message = "La categoría es obligatoria")
        Long idCategoria

) {
}
