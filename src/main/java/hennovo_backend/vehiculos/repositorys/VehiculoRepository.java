package hennovo_backend.vehiculos.repositorys;

import hennovo_backend.vehiculos.entitys.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    List<Vehiculo> findByActivoTrue();

    Optional<Vehiculo> findByPatente(String patente);

    boolean existsByPatente(String patente);
}