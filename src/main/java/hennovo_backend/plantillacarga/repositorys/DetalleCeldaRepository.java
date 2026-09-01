package hennovo_backend.plantillacarga.repositorys;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hennovo_backend.plantillacarga.entitys.DetalleCelda;

public interface DetalleCeldaRepository extends JpaRepository<DetalleCelda, Long> {

    @Query("""
        SELECT DISTINCT d.fecha
        FROM DetalleCelda d
        WHERE d.celda.plantilla.vehiculo.id = :vehiculoId
        ORDER BY d.fecha DESC
    """)
    List<LocalDate> findFechasDistintasByVehiculoId(@Param("vehiculoId") Long vehiculoId);
}
