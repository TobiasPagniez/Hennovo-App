package hennovo_backend.cheques.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChequeResponse(

        Long id,
        LocalDate fechaIngreso,
        Long clienteId,
        String clienteNombre,
        String titular,
        String codigoBanco,
        String nombreBanco,
        BigDecimal importe,
        LocalDate fechaPago,
        Boolean endosado,
        Boolean firmaTitular,
        Boolean activo

) {}
