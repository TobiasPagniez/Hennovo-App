package hennovo_backend.clientes.dtos.request;

import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String nombre,

        @NotBlank(message = "La dirección es obligatoria")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String direccion,

        @NotBlank(message = "La localidad es obligatoria")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String localidad,

        @NotBlank(message = "El teléfono es obligatorio")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String telefono,

        @NotNull(message = "La categoría es obligatoria")
        Long idCategoria

) {
}
