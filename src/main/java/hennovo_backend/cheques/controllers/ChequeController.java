package hennovo_backend.cheques.controllers;

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

import hennovo_backend.cheques.dtos.request.ChequeRequest;
import hennovo_backend.cheques.dtos.response.ChequeResponse;
import hennovo_backend.cheques.services.interfaces.ChequeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cheques")
@RequiredArgsConstructor
public class ChequeController {

    private final ChequeService chequeService;

    @PostMapping
    public ResponseEntity<ChequeResponse> crear(
            @Valid @RequestBody ChequeRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(chequeService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ChequeResponse>> listar() {

        return ResponseEntity.ok(chequeService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChequeResponse> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(chequeService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChequeResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ChequeRequest request) {

        return ResponseEntity.ok(chequeService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long id) {

        chequeService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ChequeResponse> reactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(chequeService.reactivar(id));
    }
}
