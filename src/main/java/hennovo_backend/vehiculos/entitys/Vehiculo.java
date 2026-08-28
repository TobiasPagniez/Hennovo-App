package hennovo_backend.vehiculos.entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vehiculos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String patente;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    private Integer anio;

    @Column(nullable = false)
    @Builder.Default
    private Integer kilometrajeActual = 0;

    // Mantenimiento por fecha (tramites y documentacion)
    private LocalDate proximoServiceFecha;
    private LocalDate vencimientoSeguro;
    private LocalDate vencimientoItv;
    private LocalDate vencimientoSenasa;

    // Mantenimiento por kilometraje 
    private Integer proximoCambioAceiteKm;
    private Integer proximaRotacionAlineadoKm;
    private Integer proximoCambioCorreaKm;

    @Column(length = 500)
    private String observacionesMantenimiento;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
