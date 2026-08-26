package hennovo_backend.planillaventas.dtos.response;

public record TopeProductoDTO(
        Long productoId,
        String producto,
        Integer cantidad
) {}
