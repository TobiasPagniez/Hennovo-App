package hennovo_backend.auth.repositorys;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.auth.entitys.NombreRol;
import hennovo_backend.auth.entitys.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(NombreRol nombre);

    // para crear admin
    boolean existsByNombre(NombreRol nombre);

}
