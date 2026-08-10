package hennovo_backend.vehiculos.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ActualizarKilometrajeRequest {

    @NotNull(message = "El kilometraje es obligatorio")
    @Positive(message = "El kilometraje debe ser mayor a 0")
    private Integer kilometraje;

    private String observacion;
}