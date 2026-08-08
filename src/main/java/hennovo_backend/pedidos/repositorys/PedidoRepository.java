package hennovo_backend.pedidos.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.pedidos.entitys.Pedido;


public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
