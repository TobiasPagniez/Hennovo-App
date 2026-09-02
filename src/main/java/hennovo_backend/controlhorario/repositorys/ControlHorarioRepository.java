package hennovo_backend.controlhorario.repositorys;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.controlhorario.entitys.ControlHorario;

public interface ControlHorarioRepository extends JpaRepository<ControlHorario, Long> {

    List<ControlHorario> findByUsuarioId(Long usuarioId);

    List<ControlHorario> findByUsuarioIdAndFechaBetween(
            Long usuarioId, LocalDate desde, LocalDate hasta);

    List<ControlHorario> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
