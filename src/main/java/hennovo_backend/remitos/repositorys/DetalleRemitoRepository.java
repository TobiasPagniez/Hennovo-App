package hennovo_backend.remitos.repositorys;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.remitos.entitys.DetalleRemito;

public interface DetalleRemitoRepository
        extends JpaRepository<DetalleRemito, Long> {

    List<DetalleRemito> findByRemitoId(Long remitoId);

 
void deleteByRemitoId(Long remitoId);
}
