package hennovo_backend.productos.exceptions;

import hennovo_backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ProductoNoEncontradoException extends CustomException {

    public ProductoNoEncontradoException(Long id) {
        super(
                "Producto no encontrado.",
                HttpStatus.NOT_FOUND,
                List.of("No existe un producto con id " + id + ".")
        );
    }
}
