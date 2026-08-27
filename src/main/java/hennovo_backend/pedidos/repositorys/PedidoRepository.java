package hennovo_backend.pedidos.repositorys;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.pedidos.entitys.Pedido;


public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByFecha(LocalDate fecha);

    //para el modulo de ruta
    List<Pedido> findByRutaIdOrderByOrdenRutaAsc(Long rutaId);

    List<Pedido> findByFechaAndRutaIsNull(LocalDate fecha);

    //para el modulo de pagos
    List<Pedido> findByClienteIdOrderByFechaAscIdAsc(Long clienteId);

    List<Pedido> findByFechaAndUsuarioId(LocalDate fecha, Long usuarioId);
}
