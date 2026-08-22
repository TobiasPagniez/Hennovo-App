package hennovo_backend.vehiculos.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateVehiculoRequest {

    @NotBlank(message = "La patente es obligatoria")
    private String patente;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    private Integer anio;

    @NotNull(message = "El kilometraje inicial es obligatorio")
    @PositiveOrZero(message = "El kilometraje no puede ser negativo")
    private Integer kilometrajeActual;
}