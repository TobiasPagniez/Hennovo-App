package hennovo_backend.remitos.controllers;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hennovo_backend.remitos.dtos.request.RemitoRequest;
import hennovo_backend.remitos.dtos.response.RemitoResponse;
import hennovo_backend.remitos.pdf.pdfinterface.RemitoPdfService;
import hennovo_backend.remitos.services.interfaces.RemitoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/remitos")
@RequiredArgsConstructor
public class RemitoController {

    private final RemitoService remitoService;
    private final RemitoPdfService remitoPdfService;

    @PostMapping
    public ResponseEntity<RemitoResponse> crear(
            @Valid @RequestBody RemitoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(remitoService.crear(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RemitoResponse> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                remitoService.obtenerPorId(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<RemitoResponse>> listar() {

        return ResponseEntity.ok(
                remitoService.listar()
        );
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<RemitoResponse> obtenerPorPedido(
            @PathVariable Long pedidoId) {

        return ResponseEntity.ok(
                remitoService.obtenerPorPedido(pedidoId)
        );
    }

        @GetMapping("/{id}/pdf")
        public ResponseEntity<byte[]> generarPdf(
                @PathVariable Long id) {

        byte[] pdf = remitoPdfService.generarPdf(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=remito-" + id + ".pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
        }    
}