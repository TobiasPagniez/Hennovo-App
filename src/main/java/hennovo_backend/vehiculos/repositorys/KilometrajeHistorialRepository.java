package hennovo_backend.vehiculos.repositorys;

import hennovo_backend.vehiculos.entitys.KilometrajeHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KilometrajeHistorialRepository extends JpaRepository<KilometrajeHistorial, Long> {

    List<KilometrajeHistorial> findByVehiculo_IdOrderByFechaDesc(Long vehiculoId);
}