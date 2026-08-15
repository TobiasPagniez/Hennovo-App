package hennovo_backend.remitos.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.remitos.entitys.Remito;

   
public interface RemitoRepository extends JpaRepository<Remito, Long> {

    Optional<Remito> findByPedidoId(Long pedidoId); 

    List<Remito> findByPedidoClienteIdOrderByFechaDescIdDesc(Long clienteId);
}