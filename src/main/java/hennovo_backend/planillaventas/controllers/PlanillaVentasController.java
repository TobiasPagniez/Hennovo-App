package hennovo_backend.planillaventas.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.planillaventas.dtos.request.PlanillaVentasOrdenRequest;
import hennovo_backend.planillaventas.dtos.response.PlanillaVentasClienteDTO;
import hennovo_backend.planillaventas.services.interfaces.PlanillaVentasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/planilla-ventas")
@RequiredArgsConstructor
public class PlanillaVentasController {

    private final PlanillaVentasService planillaVentasService;

    @GetMapping
    public ResponseEntity<List<PlanillaVentasClienteDTO>> obtener(
            @RequestParam Long usuarioId,
            @RequestParam LocalDate fecha) {

        return ResponseEntity.ok(planillaVentasService.obtenerPlanilla(usuarioId, fecha));
    }

    @PutMapping("/orden")
    public ResponseEntity<Void> guardarOrden(
            @Valid @RequestBody PlanillaVentasOrdenRequest request) {

        planillaVentasService.guardarOrden(request);

        return ResponseEntity.noContent().build();
    }
}
