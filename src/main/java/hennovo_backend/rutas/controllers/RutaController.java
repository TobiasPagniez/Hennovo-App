package hennovo_backend.rutas.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.rutas.dtos.request.RutaRequest;
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
}
