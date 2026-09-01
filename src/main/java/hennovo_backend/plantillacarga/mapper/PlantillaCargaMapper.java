package hennovo_backend.plantillacarga.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import hennovo_backend.plantillacarga.dtos.response.CeldaPlantillaResponse;
import hennovo_backend.plantillacarga.dtos.response.DetalleCeldaResponse;
import hennovo_backend.plantillacarga.dtos.response.PlantillaCargaResponse;
import hennovo_backend.plantillacarga.entitys.CeldaPlantilla;
import hennovo_backend.plantillacarga.entitys.DetalleCelda;
import hennovo_backend.plantillacarga.entitys.PlantillaCarga;

@Component
public class PlantillaCargaMapper {

    public PlantillaCargaResponse toResponse(PlantillaCarga plantilla, LocalDate fecha) {

        return new PlantillaCargaResponse(
                plantilla.getId(),
                plantilla.getNombre(),
                plantilla.getFilas(),
                plantilla.getColumnas(),
                plantilla.getNivel().name(),
                plantilla.getVehiculo().getId(),
                plantilla.getCeldas().stream()
                        .map(celda -> toCeldaResponse(celda, fecha))
                        .toList()
        );
    }

    private CeldaPlantillaResponse toCeldaResponse(CeldaPlantilla celda, LocalDate fecha) {

        return new CeldaPlantillaResponse(
                celda.getId(),
                celda.getFila(),
                celda.getColumna(),
                celda.getDetalles().stream()
                        .filter(d -> d.getFecha().equals(fecha))
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
