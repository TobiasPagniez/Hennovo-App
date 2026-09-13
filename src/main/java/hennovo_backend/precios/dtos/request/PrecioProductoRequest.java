package hennovo_backend.precios.dtos.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record PrecioProductoRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @NotNull(message = "La categoría de cliente es obligatoria")
        Long categoriaId,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.00", inclusive = false, message = "El precio debe ser mayor a cero")
        @Digits(integer = 10, fraction = 2, message = "El precio debe tener hasta 10 enteros y 2 decimales")
        BigDecimal precio
) {
}
