package hennovo_backend.vehiculos.dtos.request;

import hennovo_backend.shared.validation.NoHtml;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateVehiculoRequest {

    @NoHtml(message = "No se permiten etiquetas HTML en este campo")
    private String patente;

    @NoHtml(message = "No se permiten etiquetas HTML en este campo")
    private String marca;

    @NoHtml(message = "No se permiten etiquetas HTML en este campo")
    private String modelo;

    private Integer anio;

    private LocalDate proximoServiceFecha;
    private LocalDate vencimientoSeguro;
    private LocalDate vencimientoItv;
    private LocalDate vencimientoSenasa;

    private Integer proximoCambioAceiteKm;
    private Integer proximaRotacionAlineadoKm;
    private Integer proximoCambioCorreaKm;

    @NoHtml(message = "No se permiten etiquetas HTML en este campo")
    private String observacionesMantenimiento;
}
