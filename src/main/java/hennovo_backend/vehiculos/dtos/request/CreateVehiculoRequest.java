package hennovo_backend.vehiculos.dtos.request;

import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateVehiculoRequest {

    @NotBlank(message = "La patente es obligatoria")
    @NoHtml(message = "No se permiten etiquetas HTML en este campo")
    private String patente;

    @NotBlank(message = "La marca es obligatoria")
    @NoHtml(message = "No se permiten etiquetas HTML en este campo")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @NoHtml(message = "No se permiten etiquetas HTML en este campo")
    private String modelo;

    private Integer anio;

    @NotNull(message = "El kilometraje inicial es obligatorio")
    @PositiveOrZero(message = "El kilometraje no puede ser negativo")
    private Integer kilometrajeActual;
}