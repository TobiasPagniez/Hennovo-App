package hennovo_backend.auth.repositorys;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.auth.entitys.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

}