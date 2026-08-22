package hennovo_backend.rutas.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.rutas.dtos.request.AsignarPedidosRequest;
import hennovo_backend.rutas.dtos.request.RutaRequest;
import hennovo_backend.rutas.dtos.response.RutaPedidoResponse;
import hennovo_backend.rutas.dtos.response.RutaResponse;
import hennovo_backend.rutas.services.interfaces.RutaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    @PostMapping
    public ResponseEntity<RutaResponse> crear(
            @Valid @RequestBody RutaRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rutaService.crear(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutaResponse> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                rutaService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<RutaResponse>> listar() {

        return ResponseEntity.ok(
                rutaService.listar());
    }
    @GetMapping("/pedidos-disponibles")
    public ResponseEntity<List<RutaPedidoResponse>> listarPedidosDisponibles(
            @RequestParam LocalDate fecha) {

        return ResponseEntity.ok(
                rutaService.listarPedidosDisponibles(fecha));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RutaRequest request) {

        return ResponseEntity.ok(
                rutaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long id) {

        rutaService.desactivar(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<RutaPedidoResponse>> listarPedidos(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                rutaService.listarPedidos(id));
    }

    @PutMapping("/{id}/pedidos")
    public ResponseEntity<List<RutaPedidoResponse>> asignarPedidos(
            @PathVariable Long id,
            @Valid @RequestBody AsignarPedidosRequest request) {

        return ResponseEntity.ok(
                rutaService.asignarPedidos(id, request));
    }

    @DeleteMapping("/{rutaId}/pedidos/{pedidoId}")
    public ResponseEntity<Void> quitarPedido(
            @PathVariable Long rutaId,
            @PathVariable Long pedidoId) {

        rutaService.quitarPedido(rutaId, pedidoId);

        return ResponseEntity.noContent().build();
    }
}
