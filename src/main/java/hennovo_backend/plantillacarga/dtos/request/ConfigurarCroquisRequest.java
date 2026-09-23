package hennovo_backend.plantillacarga.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ConfigurarCroquisRequest(

        @NotNull(message = "Las filas son obligatorias")
        @Min(value = 2, message = "Debe haber al menos 2 filas")
        @Max(value = 20, message = "No puede haber más de 20 filas")
        Integer filas,

        @NotNull(message = "Las columnas son obligatorias")
        @Min(value = 2, message = "Debe haber al menos 2 columnas")
        @Max(value = 20, message = "No puede haber más de 20 columnas")
        Integer columnas,

        @NotNull(message = "Debe indicar si se usa nivel superior")
        Boolean incluirNivelSuperior,

        Boolean confirmarPerdidaDatos

) {
    public Boolean confirmarPerdidaDatos() {
        return Boolean.TRUE.equals(confirmarPerdidaDatos);
    }
}
