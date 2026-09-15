package hennovo_backend.pagos.dtos.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import hennovo_backend.pagos.entitys.MedioPago;
import hennovo_backend.shared.validation.NoHtml;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PagoRequestDTO(

        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "El importe es obligatorio")
        @DecimalMin(value = "0.01", message = "El importe debe ser mayor a 0")
        BigDecimal importe,

        @NotNull(message = "El medio de pago es obligatorio")
        MedioPago medioPago,

        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        @Size(max = 100, message = "El número de comprobante no puede superar los 100 caracteres")
        String numeroComprobante,

        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
        String observaciones,

        // Datos del cheque la validacion not null va dentro del service
        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String titular,

        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String codigoBanco,

        @NoHtml(message = "No se permiten etiquetas HTML en este campo")
        String nombreBanco,

        LocalDate fechaPago,

        Boolean endosado,

        Boolean firmaTitular

) {
}
