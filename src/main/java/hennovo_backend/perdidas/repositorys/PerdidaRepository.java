package hennovo_backend.perdidas.repositorys;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.perdidas.entitys.Perdida;

public interface PerdidaRepository extends JpaRepository<Perdida, Long> {

    List<Perdida> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
