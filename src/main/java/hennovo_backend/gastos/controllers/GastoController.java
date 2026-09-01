package hennovo_backend.gastos.controllers;

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

import hennovo_backend.gastos.dtos.request.GastoRequest;
import hennovo_backend.gastos.dtos.response.GastoResponse;
import hennovo_backend.gastos.services.interfaces.GastoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService gastoService;

    @PostMapping
    public ResponseEntity<GastoResponse> crear(
            @Valid @RequestBody GastoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gastoService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<GastoResponse>> listar(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {

        if (desde != null && hasta != null) {
            return ResponseEntity.ok(gastoService.listarPorRangoFechas(desde, hasta));
        }

        return ResponseEntity.ok(gastoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoResponse> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(gastoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody GastoRequest request) {

        return ResponseEntity.ok(gastoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        gastoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
