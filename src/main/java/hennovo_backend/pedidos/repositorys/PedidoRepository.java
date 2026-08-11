package hennovo_backend.pedidos.repositorys;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.pedidos.entitys.Pedido;


public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    //para el modulo de ruta
    List<Pedido> findByRutaIdOrderByOrdenRutaAsc(Long rutaId);

    List<Pedido> findByFechaAndRutaIsNull(LocalDate fecha);
}
