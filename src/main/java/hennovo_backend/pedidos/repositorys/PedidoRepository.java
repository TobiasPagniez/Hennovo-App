package hennovo_backend.pedidos.repositorys;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.pedidos.entitys.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByFecha(LocalDate fecha);

    // Paginacion y busqueda
    Page<Pedido> findByFecha(LocalDate fecha, Pageable pageable);

    Page<Pedido> findByClienteNombreContainingIgnoreCase(
            String nombre,
            Pageable pageable);

    Page<Pedido> findByFechaAndClienteNombreContainingIgnoreCase(
            LocalDate fecha,
            String nombre,
            Pageable pageable);

    // Para el modulo de ruta
    List<Pedido> findByRutaIdOrderByOrdenRutaAsc(Long rutaId);

    List<Pedido> findByFechaAndRutaIsNull(LocalDate fecha);

    // Para el modulo de pagos
    List<Pedido> findByClienteIdOrderByFechaAscIdAsc(Long clienteId);

    List<Pedido> findByFechaAndUsuarioId(
            LocalDate fecha,
            Long usuarioId);
}
