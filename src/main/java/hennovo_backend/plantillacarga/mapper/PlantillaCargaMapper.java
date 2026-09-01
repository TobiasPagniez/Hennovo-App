package hennovo_backend.plantillacarga.mapper;

import org.springframework.stereotype.Component;

import hennovo_backend.plantillacarga.dtos.response.CeldaPlantillaResponse;
import hennovo_backend.plantillacarga.dtos.response.DetalleCeldaResponse;
import hennovo_backend.plantillacarga.dtos.response.PlantillaCargaResponse;
import hennovo_backend.plantillacarga.entitys.CeldaPlantilla;
import hennovo_backend.plantillacarga.entitys.DetalleCelda;
import hennovo_backend.plantillacarga.entitys.PlantillaCarga;

@Component
public class PlantillaCargaMapper {

    public PlantillaCargaResponse toResponse(PlantillaCarga plantilla) {

        return new PlantillaCargaResponse(
                plantilla.getId(),
                plantilla.getNombre(),
                plantilla.getFilas(),
                plantilla.getColumnas(),
                plantilla.getNivel().name(),
                plantilla.getVehiculo().getId(),
                plantilla.getCeldas().stream()
                        .map(this::toCeldaResponse)
                        .toList()
        );
    }

    private CeldaPlantillaResponse toCeldaResponse(CeldaPlantilla celda) {

        return new CeldaPlantillaResponse(
                celda.getId(),
                celda.getFila(),
                celda.getColumna(),
                celda.getDetalles().stream()
                        .map(this::toDetalleResponse)
                        .toList()
        );
    }

    private DetalleCeldaResponse toDetalleResponse(DetalleCelda detalle) {

        return new DetalleCeldaResponse(
                detalle.getId(),
                detalle.getProducto().getId(),
                detalle.getCantidad()
        );
    }
}
