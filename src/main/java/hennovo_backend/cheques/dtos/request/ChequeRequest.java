package hennovo_backend.cheques.dtos.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChequeRequest(

        @NotNull(message = "La fecha de ingreso es obligatoria")
        LocalDate fechaIngreso,

        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotBlank(message = "El titular es obligatorio")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String titular,

        @NotBlank(message = "El código de banco es obligatorio")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String codigoBanco,

        @NotBlank(message = "El nombre del banco es obligatorio")
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String nombreBanco,

        @NotNull(message = "El importe es obligatorio")
        @DecimalMin(value = "0.01", message = "El importe debe ser mayor a 0")
        BigDecimal importe,

        @NotNull(message = "La fecha de pago es obligatoria")
        LocalDate fechaPago,

        @NotNull(message = "Debe indicar si está endosado")
        Boolean endosado,

        @NotNull(message = "Debe indicar si tiene firma del titular")
        Boolean firmaTitular

) {}
