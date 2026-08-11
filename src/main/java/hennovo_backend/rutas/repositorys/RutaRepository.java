package hennovo_backend.rutas.repositorys;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.rutas.entitys.Ruta;

public interface RutaRepository extends JpaRepository<Ruta, Long> {

    List<Ruta> findByFecha(LocalDate fecha);

    List<Ruta> findByUsuarioId(Long usuarioId);

    List<Ruta> findByFechaAndUsuarioId(
            LocalDate fecha,
            Long usuarioId);

    List<Ruta> findByActivaTrue();
}
