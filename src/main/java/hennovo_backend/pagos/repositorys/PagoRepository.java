package hennovo_backend.pagos.repositorys;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.pagos.entitys.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByClienteIdOrderByFechaAsc(Long clienteId);

    List<Pago> findByClienteIdAndAnuladoFalseOrderByFechaAsc(Long clienteId);

}