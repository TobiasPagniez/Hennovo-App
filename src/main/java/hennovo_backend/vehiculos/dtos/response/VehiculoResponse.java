package hennovo_backend.vehiculos.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculoResponse {

    private Long id;
    private String patente;
    private String marca;
    private String modelo;
    private Integer anio;
    private Integer kilometrajeActual;
    private LocalDate proximoServiceFecha;
    private LocalDate proximoCambioAceiteFecha;
    private String observacionesMantenimiento;
    private Boolean activo;
}