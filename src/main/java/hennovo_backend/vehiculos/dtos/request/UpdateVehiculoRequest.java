package hennovo_backend.vehiculos.dtos.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateVehiculoRequest {

    private String patente;
    private String marca;
    private String modelo;
    private Integer anio;
    private LocalDate proximoServiceFecha;
    private LocalDate proximoCambioAceiteFecha;
    private String observacionesMantenimiento;
}