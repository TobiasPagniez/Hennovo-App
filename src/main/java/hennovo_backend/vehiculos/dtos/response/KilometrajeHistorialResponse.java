package hennovo_backend.vehiculos.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KilometrajeHistorialResponse {

    private Long id;
    private Long vehiculoId;
    private Integer kilometraje;
    private LocalDateTime fecha;
    private String observacion;
}