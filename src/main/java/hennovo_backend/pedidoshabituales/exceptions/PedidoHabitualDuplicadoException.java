package hennovo_backend.pedidoshabituales.exceptions;

import hennovo_backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PedidoHabitualDuplicadoException extends CustomException {

    public PedidoHabitualDuplicadoException() {
        super(
                "El producto ya forma parte del pedido habitual del cliente.",
                HttpStatus.CONFLICT,
                List.of("No se puede agregar el mismo producto dos veces.")
        );
    }
}