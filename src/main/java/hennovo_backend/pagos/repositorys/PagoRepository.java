package hennovo_backend.pagos.repositorys;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hennovo_backend.pagos.entitys.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByClienteIdOrderByFechaAsc(Long clienteId);

    List<Pago> findByClienteIdAndAnuladoFalseOrderByFechaAsc(
            Long clienteId);

    @Query("""
            SELECT p.cliente.id, COALESCE(SUM(p.importe), 0)
            FROM Pago p
            WHERE p.cliente.id IN :clienteIds
              AND p.anulado = false
            GROUP BY p.cliente.id
            """)
    List<Object[]> calcularTotalesPorCliente(
            @Param("clienteIds") List<Long> clienteIds);
}
