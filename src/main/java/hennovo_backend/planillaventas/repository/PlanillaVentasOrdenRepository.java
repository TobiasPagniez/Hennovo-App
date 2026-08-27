package hennovo_backend.planillaventas.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.planillaventas.entitys.PlanillaVentasOrden;

public interface PlanillaVentasOrdenRepository extends JpaRepository<PlanillaVentasOrden, Long> {

    List<PlanillaVentasOrden> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

    Optional<PlanillaVentasOrden> findByUsuarioIdAndClienteIdAndFecha(
            Long usuarioId, Long clienteId, LocalDate fecha);
}
