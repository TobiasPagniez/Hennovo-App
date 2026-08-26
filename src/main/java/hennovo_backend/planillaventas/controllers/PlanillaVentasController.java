package hennovo_backend.planillaventas.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.planillaventas.dtos.response.PlanillaVentasClienteDTO;
import hennovo_backend.planillaventas.services.interfaces.PlanillaVentasService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/planilla-ventas")
@RequiredArgsConstructor
public class PlanillaVentasController {

    private final PlanillaVentasService planillaVentasService;

    @GetMapping
    public ResponseEntity<List<PlanillaVentasClienteDTO>> obtener(
            @RequestParam LocalDate fecha) {

        return ResponseEntity.ok(planillaVentasService.obtenerPlanilla(fecha));
    }
}
