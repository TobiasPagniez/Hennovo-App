package hennovo_backend.controlhorario.controllers;

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

import hennovo_backend.controlhorario.dtos.request.ControlHorarioRequest;
import hennovo_backend.controlhorario.dtos.response.ControlHorarioResponse;
import hennovo_backend.controlhorario.dtos.response.ResumenHorasDTO;
import hennovo_backend.controlhorario.services.interfaces.ControlHorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/control-horario")
@RequiredArgsConstructor
public class ControlHorarioController {

    private final ControlHorarioService controlHorarioService;

    @PostMapping
    public ResponseEntity<ControlHorarioResponse> crear(
            @Valid @RequestBody ControlHorarioRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(controlHorarioService.crear(request));
    }

    @GetMapping("/mios")
    public ResponseEntity<List<ControlHorarioResponse>> listarPropios() {

        return ResponseEntity.ok(controlHorarioService.listarPropios());
    }

    @GetMapping
    public ResponseEntity<List<ControlHorarioResponse>> listarTodos(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {

        return ResponseEntity.ok(controlHorarioService.listarTodos(desde, hasta));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ControlHorarioResponse>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {

        return ResponseEntity.ok(
                controlHorarioService.listarPorUsuario(usuarioId, desde, hasta));
    }

    @GetMapping("/resumen")
    public ResponseEntity<List<ResumenHorasDTO>> resumenPorUsuario(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {

        return ResponseEntity.ok(controlHorarioService.resumenPorUsuario(desde, hasta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ControlHorarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ControlHorarioRequest request) {

        return ResponseEntity.ok(controlHorarioService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        controlHorarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
