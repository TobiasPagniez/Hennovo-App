package hennovo_backend.pagos.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import hennovo_backend.pagos.entitys.MedioPago;

public record PagoResponseDTO(

        Long id,

        LocalDate fecha,

        BigDecimal importe,

        MedioPago medioPago,

        String numeroComprobante,

        String observaciones,

        Boolean anulado,

        Long clienteId

) {
}
