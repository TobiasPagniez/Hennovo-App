package hennovo_backend.pedidoshabituales.controller;

import hennovo_backend.pedidoshabituales.dtos.request.PedidoHabitualRequestDTO;
import hennovo_backend.pedidoshabituales.dtos.response.PedidoHabitualResponseDTO;
import hennovo_backend.pedidoshabituales.services.interfaces.PedidoHabitualService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos-habituales")
@RequiredArgsConstructor
public class PedidoHabitualController {

    private final PedidoHabitualService pedidoHabitualService;

    @PostMapping
    public ResponseEntity<PedidoHabitualResponseDTO> crear(
            @Valid @RequestBody PedidoHabitualRequestDTO dto) {

        PedidoHabitualResponseDTO pedidoHabitual = pedidoHabitualService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoHabitual);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoHabitualResponseDTO> obtenerPorId(@PathVariable Long id) {

        return ResponseEntity.ok(pedidoHabitualService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoHabitualResponseDTO>> obtenerPorCliente(
            @PathVariable Long idCliente) {

        return ResponseEntity.ok(pedidoHabitualService.obtenerPorCliente(idCliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoHabitualResponseDTO> modificar(
            @PathVariable Long id,
            @Valid @RequestBody PedidoHabitualRequestDTO dto) {

        return ResponseEntity.ok(pedidoHabitualService.modificar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        pedidoHabitualService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
