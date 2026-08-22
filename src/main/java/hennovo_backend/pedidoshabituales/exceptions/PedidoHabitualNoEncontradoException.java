package hennovo_backend.pedidoshabituales.exceptions;

import hennovo_backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PedidoHabitualNoEncontradoException extends CustomException {

    public PedidoHabitualNoEncontradoException(Long id) {
        super(
                "Pedido habitual no encontrado.",
                HttpStatus.NOT_FOUND,
                List.of("No existe un pedido habitual con id " + id + ".")
        );
    }
}
