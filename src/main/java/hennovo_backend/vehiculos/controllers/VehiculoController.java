package hennovo_backend.vehiculos.controllers;

import hennovo_backend.vehiculos.dtos.request.ActualizarKilometrajeRequest;
import hennovo_backend.vehiculos.dtos.request.CreateVehiculoRequest;
import hennovo_backend.vehiculos.dtos.request.UpdateVehiculoRequest;
import hennovo_backend.vehiculos.dtos.response.KilometrajeHistorialResponse;
import hennovo_backend.vehiculos.dtos.response.VehiculoResponse;
import hennovo_backend.vehiculos.services.interfaces.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    // registrar vehiculo (RF-28)
    @PostMapping
    public ResponseEntity<VehiculoResponse> crear(@Valid @RequestBody CreateVehiculoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<VehiculoResponse>> listar() {
        return ResponseEntity.ok(vehiculoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponse> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateVehiculoRequest request) {
        return ResponseEntity.ok(vehiculoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        vehiculoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    //  actualizar kilometraje (RF-29)
    @PatchMapping("/{id}/kilometraje")
    public ResponseEntity<VehiculoResponse> actualizarKilometraje(@PathVariable Long id,
                                                                    @Valid @RequestBody ActualizarKilometrajeRequest request) {
        return ResponseEntity.ok(vehiculoService.actualizarKilometraje(id, request));
    }

    @GetMapping("/{id}/kilometraje/historial")
    public ResponseEntity<List<KilometrajeHistorialResponse>> historialKilometraje(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.historialKilometraje(id));
    }

    // RF-31: consultar vencimientos (por defecto, proximos 15 dias)
    @GetMapping("/vencimientos")
    public ResponseEntity<List<VehiculoResponse>> consultarVencimientos(
            @RequestParam(defaultValue = "15") int diasAnticipacion) {
        return ResponseEntity.ok(vehiculoService.consultarVencimientos(diasAnticipacion));
    }
}