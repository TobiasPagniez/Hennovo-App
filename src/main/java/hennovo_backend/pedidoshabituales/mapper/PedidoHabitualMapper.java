package hennovo_backend.pedidoshabituales.mapper;

import hennovo_backend.pedidoshabituales.dtos.response.PedidoHabitualResponseDTO;
import hennovo_backend.pedidoshabituales.entitys.PedidoHabitual;
import org.springframework.stereotype.Component;

@Component
public class PedidoHabitualMapper {

    public PedidoHabitualResponseDTO toResponseDTO(PedidoHabitual pedidoHabitual) {

        return new PedidoHabitualResponseDTO(
                pedidoHabitual.getId(),
                pedidoHabitual.getCliente().getId(),
                pedidoHabitual.getCliente().getNombre(),
                pedidoHabitual.getProducto().getId(),
                pedidoHabitual.getProducto().getTipoHuevo()
                        + " - "
                        + pedidoHabitual.getProducto().getTamaño()
                        + " - "
                        + pedidoHabitual.getProducto().getPresentacion(),
                pedidoHabitual.getCantidad()
        );
    }
}
