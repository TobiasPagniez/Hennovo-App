package hennovo_backend.caregoriaCliente.controllers;

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
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.caregoriaCliente.dtos.request.CategoriaClienteRequestDTO;
import hennovo_backend.caregoriaCliente.dtos.response.CategoriaClienteResponseDTO;
import hennovo_backend.caregoriaCliente.services.interfaces.CategoriaClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias-clientes")
@RequiredArgsConstructor
public class CategoriaClienteController {

    private final CategoriaClienteService categoriaClienteService;

    @PostMapping
    public ResponseEntity<CategoriaClienteResponseDTO> crear(
            @Valid @RequestBody CategoriaClienteRequestDTO dto) {

        CategoriaClienteResponseDTO categoria =
                categoriaClienteService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoria);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaClienteResponseDTO>> obtenerTodos() {

        List<CategoriaClienteResponseDTO> categorias =
                categoriaClienteService.obtenerTodos();

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaClienteResponseDTO> obtenerPorId(
            @PathVariable Long id) {

        CategoriaClienteResponseDTO categoria =
                categoriaClienteService.obtenerPorId(id);

        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaClienteResponseDTO> modificar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaClienteRequestDTO dto) {

        CategoriaClienteResponseDTO categoria =
                categoriaClienteService.modificar(id, dto);

        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        categoriaClienteService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
