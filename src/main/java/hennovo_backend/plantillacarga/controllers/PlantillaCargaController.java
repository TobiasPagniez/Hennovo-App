package hennovo_backend.plantillacarga.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.plantillacarga.dtos.request.ActualizarCantidadDetalleRequest;
import hennovo_backend.plantillacarga.dtos.request.AgregarDetalleCeldaRequest;
import hennovo_backend.plantillacarga.dtos.request.ConfigurarCroquisRequest;
import hennovo_backend.plantillacarga.dtos.request.CopiarCroquisRequest;
import hennovo_backend.plantillacarga.dtos.request.MoverDetalleCeldaRequest;
import hennovo_backend.plantillacarga.dtos.response.PlantillaCargaResponse;
import hennovo_backend.plantillacarga.services.interfaces.PlantillaCargaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlantillaCargaController {

    private final PlantillaCargaService plantillaCargaService;

    @GetMapping("/api/vehiculos/{vehiculoId}/plantillas")
    public ResponseEntity<List<PlantillaCargaResponse>> obtenerPorVehiculo(
            @PathVariable Long vehiculoId,
            @RequestParam LocalDate fecha) {

        return ResponseEntity.ok(plantillaCargaService.obtenerPorVehiculo(vehiculoId, fecha));
    }

    @PutMapping("/api/vehiculos/{vehiculoId}/plantillas/configurar")
    public ResponseEntity<List<PlantillaCargaResponse>> configurar(
            @PathVariable Long vehiculoId,
            @Valid @RequestBody ConfigurarCroquisRequest request) {

        return ResponseEntity.ok(plantillaCargaService.configurar(vehiculoId, request));
    }

    @PostMapping("/api/vehiculos/{vehiculoId}/plantillas/copiar")
    public ResponseEntity<Void> copiarDia(
            @PathVariable Long vehiculoId,
            @Valid @RequestBody CopiarCroquisRequest request) {

        plantillaCargaService.copiarDia(vehiculoId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/vehiculos/{vehiculoId}/plantillas/fechas-con-contenido")
    public ResponseEntity<List<LocalDate>> obtenerFechasConContenido(
            @PathVariable Long vehiculoId) {

        return ResponseEntity.ok(plantillaCargaService.obtenerFechasConContenido(vehiculoId));
    }

    @PostMapping("/api/plantillas-carga/celdas/{celdaId}/detalles")
    public ResponseEntity<Void> agregarDetalle(
            @PathVariable Long celdaId,
            @Valid @RequestBody AgregarDetalleCeldaRequest request) {

        plantillaCargaService.agregarDetalle(celdaId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/plantillas-carga/detalles/{detalleId}/mover")
    public ResponseEntity<Void> moverDetalle(
            @PathVariable Long detalleId,
            @Valid @RequestBody MoverDetalleCeldaRequest request) {

        plantillaCargaService.moverDetalle(detalleId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/plantillas-carga/detalles/{detalleId}")
    public ResponseEntity<Void> actualizarCantidad(
            @PathVariable Long detalleId,
            @Valid @RequestBody ActualizarCantidadDetalleRequest request) {

        plantillaCargaService.actualizarCantidad(detalleId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/plantillas-carga/detalles/{detalleId}")
    public ResponseEntity<Void> eliminarDetalle(
            @PathVariable Long detalleId) {

        plantillaCargaService.eliminarDetalle(detalleId);
        return ResponseEntity.noContent().build();
    }
}
