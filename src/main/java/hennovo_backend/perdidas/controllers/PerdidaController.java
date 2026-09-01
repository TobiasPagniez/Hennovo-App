package hennovo_backend.perdidas.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.perdidas.dtos.request.PerdidaRequest;
import hennovo_backend.perdidas.dtos.response.PerdidaResponse;
import hennovo_backend.perdidas.services.interfaces.PerdidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/perdidas")
@RequiredArgsConstructor
public class PerdidaController {

    private final PerdidaService perdidaService;

    @PostMapping
    public ResponseEntity<PerdidaResponse> crear(
            @Valid @RequestBody PerdidaRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(perdidaService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<PerdidaResponse>> listar(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {

        if (desde != null && hasta != null) {
            return ResponseEntity.ok(perdidaService.listarPorRangoFechas(desde, hasta));
        }

        return ResponseEntity.ok(perdidaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerdidaResponse> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(perdidaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerdidaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PerdidaRequest request) {

        return ResponseEntity.ok(perdidaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        perdidaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
