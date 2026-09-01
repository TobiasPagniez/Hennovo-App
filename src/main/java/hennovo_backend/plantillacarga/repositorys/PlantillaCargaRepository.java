package hennovo_backend.plantillacarga.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.plantillacarga.entitys.NivelCarga;
import hennovo_backend.plantillacarga.entitys.PlantillaCarga;

public interface PlantillaCargaRepository extends JpaRepository<PlantillaCarga, Long> {

    List<PlantillaCarga> findByVehiculoIdOrderByNivelAsc(Long vehiculoId);

    Optional<PlantillaCarga> findByVehiculoIdAndNivel(Long vehiculoId, NivelCarga nivel);
}
