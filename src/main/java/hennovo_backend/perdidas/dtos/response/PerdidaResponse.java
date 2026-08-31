package hennovo_backend.perdidas.dtos.response;

import java.time.LocalDate;

public record PerdidaResponse(

        Long id,
        LocalDate fecha,
        Long productoId,
        String producto,
        Integer cantidad,
        String motivo,
        String observaciones

) {}
