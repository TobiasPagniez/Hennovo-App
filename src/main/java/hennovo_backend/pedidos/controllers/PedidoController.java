package hennovo_backend.pedidos.controllers;

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

import hennovo_backend.pedidos.dtos.request.PedidoRequest;
import hennovo_backend.pedidos.dtos.response.PedidoResponse;
import hennovo_backend.pedidos.services.interfaces.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

        private final PedidoService pedidoService;

        @PostMapping
        public ResponseEntity<PedidoResponse> crear(
                        @Valid @RequestBody PedidoRequest request) {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(pedidoService.crear(request));
        }

        @GetMapping("/{id}")
        public ResponseEntity<PedidoResponse> obtenerPorId(
                        @PathVariable Long id) {
                return ResponseEntity.ok(
                                pedidoService.obtenerPorId(id));
        }

        @GetMapping
        public ResponseEntity<List<PedidoResponse>> listar() {
                return ResponseEntity.ok(
                                pedidoService.listar());
        }

        @PutMapping("/{id}")
        public ResponseEntity<PedidoResponse> actualizar(
                        @PathVariable Long id,
                        @Valid @RequestBody PedidoRequest request) {
                return ResponseEntity.ok(
                                pedidoService.actualizar(id, request));
        }

        @PatchMapping("/{id}/entregado")
        public ResponseEntity<Void> marcarComoEntregado(
                        @PathVariable Long id) {
                pedidoService.marcarComoEntregado(id);
                return ResponseEntity.noContent().build();
        }

        @PatchMapping("/{id}/pagado")
        public ResponseEntity<Void> marcarComoPagado(
                        @PathVariable Long id) {
                pedidoService.marcarComoPagado(id);
                return ResponseEntity.noContent().build();
        }
}
