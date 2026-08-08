package hennovo_backend.productos.exceptions;

import hennovo_backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ProductoDuplicadoException extends CustomException {

    public ProductoDuplicadoException() {
        super(
                "Ya existe un producto con esos datos.",
                HttpStatus.CONFLICT,
                List.of("El tipo de huevo, tamaño y presentación ya están registrados.")
        );
    }
}