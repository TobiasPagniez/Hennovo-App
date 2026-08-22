package hennovo_backend.pagos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.pagos.dtos.request.PagoRequestDTO;
import hennovo_backend.pagos.dtos.response.PagoResponseDTO;
import hennovo_backend.pagos.services.interfaces.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDTO> crear(
            @Valid @RequestBody PagoRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pagoService.crear(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                pagoService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> obtenerTodos() {

        return ResponseEntity.ok(
                pagoService.obtenerTodos());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPorCliente(
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(
                pagoService.obtenerPorCliente(clienteId));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<Void> anular(
            @PathVariable Long id) {

        pagoService.anular(id);

        return ResponseEntity.noContent().build();
    }
}
