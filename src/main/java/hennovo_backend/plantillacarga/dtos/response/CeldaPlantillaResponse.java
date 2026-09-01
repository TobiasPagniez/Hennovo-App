package hennovo_backend.plantillacarga.dtos.response;

import java.util.List;

public record CeldaPlantillaResponse(
        Long id,
        Integer fila,
        Integer columna,
        List<DetalleCeldaResponse> detalles
) {}
