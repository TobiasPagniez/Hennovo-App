package hennovo_backend.clientes.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.clientes.dtos.request.ClienteRequestDTO;
import hennovo_backend.clientes.dtos.response.ClienteResponseDTO;
import hennovo_backend.clientes.services.interfaces.ClienteService;
import hennovo_backend.pagos.dtos.response.CuentaCorrienteResponseDTO;
import hennovo_backend.pagos.services.interfaces.CuentaCorrienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final CuentaCorrienteService cuentaCorrienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crear(
            @Valid @RequestBody ClienteRequestDTO dto) {

        ClienteResponseDTO cliente = clienteService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cliente);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponseDTO>> buscarPorNombre(
            @RequestParam String nombre) {

        List<ClienteResponseDTO> clientes =
                clienteService.buscarPorNombre(nombre);

        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorId(
            @PathVariable Long id) {

        ClienteResponseDTO cliente = clienteService.obtenerPorId(id);

        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/{clienteId}/cuenta-corriente")
    public ResponseEntity<CuentaCorrienteResponseDTO> obtenerCuentaCorriente(
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(
                cuentaCorrienteService.obtenerPorCliente(clienteId));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> obtenerTodos() {

        List<ClienteResponseDTO> clientes = clienteService.obtenerTodos();

        return ResponseEntity.ok(clientes);
    }    

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> modificar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO dto) {

        ClienteResponseDTO cliente = clienteService.modificar(id, dto);

        return ResponseEntity.ok(cliente);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Long id) {

        clienteService.desactivar(id);

        return ResponseEntity.noContent().build();
    }
}
