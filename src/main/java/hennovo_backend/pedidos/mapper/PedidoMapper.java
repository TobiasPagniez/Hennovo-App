package hennovo_backend.pedidos.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.pedidos.dtos.request.PedidoRequest;
import hennovo_backend.pedidos.dtos.response.DetallePedidoResponse;
import hennovo_backend.pedidos.dtos.response.PedidoResponse;
import hennovo_backend.pedidos.entitys.Pedido;

@Component
public class PedidoMapper {

    private final DetallePedidoMapper detallePedidoMapper;

    public PedidoMapper(DetallePedidoMapper detallePedidoMapper) {
        this.detallePedidoMapper = detallePedidoMapper;
    }

    public Pedido toEntity(
            PedidoRequest request,
            Cliente cliente,
            Usuario usuario
    ) {
        Pedido pedido = new Pedido();

        pedido.setFecha(request.fecha());
        pedido.setObservaciones(request.observaciones());
        pedido.setCliente(cliente);
        pedido.setUsuario(usuario);
        pedido.setEntregado(false);
        pedido.setPagado(false);

        return pedido;
    }

    public PedidoResponse toResponse(
            Pedido pedido,
            List<DetallePedidoResponse> detalles,
            BigDecimal total
    ) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getFecha(),
                pedido.getEntregado(),
                pedido.getPagado(),
                pedido.getObservaciones(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNombre(),
                pedido.getUsuario().getId(),
                pedido.getRuta() != null ? pedido.getRuta().getId() : null,
                pedido.getOrdenRuta(),
                detalles,
                total
        );
    }
}
