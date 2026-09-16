package hennovo_backend.plantillacarga.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ConfigurarCroquisRequest(

        @NotNull(message = "Las filas son obligatorias")
        @Min(value = 1, message = "Debe haber al menos 1 fila")
        Integer filas,

        @NotNull(message = "Las columnas son obligatorias")
        @Min(value = 1, message = "Debe haber al menos 1 columna")
        Integer columnas,

        @NotNull(message = "Debe indicar si se usa nivel superior")
        Boolean incluirNivelSuperior,

        Boolean confirmarPerdidaDatos

) {
    public Boolean confirmarPerdidaDatos() {
        return Boolean.TRUE.equals(confirmarPerdidaDatos);
    }
}
