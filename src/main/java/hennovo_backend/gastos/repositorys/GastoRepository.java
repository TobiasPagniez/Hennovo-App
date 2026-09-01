package hennovo_backend.gastos.repositorys;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.gastos.entitys.Gasto;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
