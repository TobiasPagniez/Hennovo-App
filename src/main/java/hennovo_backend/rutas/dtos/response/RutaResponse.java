package hennovo_backend.rutas.dtos.response;

import java.time.LocalDate;

public record RutaResponse(

        Long id,

        LocalDate fecha,

        String nombre,

        String observaciones,

        Boolean activa,

        Long usuarioId,

        Long vehiculoId

) {
}
