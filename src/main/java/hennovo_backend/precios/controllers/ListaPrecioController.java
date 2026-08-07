package hennovo_backend.precios.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.precios.dtos.request.ListaPrecioRequest;
import hennovo_backend.precios.dtos.response.ListaPrecioResponse;
import hennovo_backend.precios.services.interfaces.ListaPrecioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/listas-precio")
@RequiredArgsConstructor
public class ListaPrecioController {

    private final ListaPrecioService listaPrecioService;

    @PostMapping
    public ResponseEntity<ListaPrecioResponse> crear(
            @Valid @RequestBody ListaPrecioRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(listaPrecioService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ListaPrecioResponse>> listar() {

        return ResponseEntity.ok(
                listaPrecioService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListaPrecioResponse> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                listaPrecioService.obtenerPorId(id)
        );
    }
}
