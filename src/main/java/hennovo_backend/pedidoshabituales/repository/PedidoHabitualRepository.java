package hennovo_backend.pedidoshabituales.repository;

import hennovo_backend.pedidoshabituales.entitys.PedidoHabitual;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoHabitualRepository extends JpaRepository<PedidoHabitual, Long> {

    List<PedidoHabitual> findByClienteId(Long clienteId);

    boolean existsByClienteIdAndProductoId(
            Long clienteId,
            Long productoId
    );
}