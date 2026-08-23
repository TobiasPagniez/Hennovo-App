package hennovo_backend.productos.controller;

import hennovo_backend.productos.dtos.ProductoRequest;
import hennovo_backend.productos.dtos.ProductoResponse;
import hennovo_backend.productos.services.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(
            @RequestBody ProductoRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {

        return ResponseEntity.ok(productoService.listarActivos());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ProductoResponse>> listarTodos() {

        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoRequest request) {

        return ResponseEntity.ok(
                productoService.actualizar(id, request)
        );
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long id) {

        productoService.desactivar(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ProductoResponse> reactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(productoService.reactivar(id));
    }

}
