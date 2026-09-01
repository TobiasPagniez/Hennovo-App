package hennovo_backend.plantillacarga.dtos.response;

import java.util.List;

public record PlantillaCargaResponse(
        Long id,
        String nombre,
        Integer filas,
        Integer columnas,
        String nivel,
        Long vehiculoId,
        List<CeldaPlantillaResponse> celdas
) {}
