package hennovo_backend.pedidos.repositorys;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import hennovo_backend.pedidos.entitys.DetallePedido;



public interface DetallePedidoRepository
        extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedidoId(Long pedidoId);

    List<DetallePedido> findByPedidoClienteId(Long clienteId);

    void deleteByPedidoId(Long pedidoId);
    List<DetallePedido> findByPedidoIdIn(List<Long> pedidoIds);

}
