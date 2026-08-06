package hennovo_backend.auth.repositorys;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hennovo_backend.auth.entitys.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    //traer el rol explicitamente para evitar el lazy
        @Query("""
        SELECT u
        FROM Usuario u
        JOIN FETCH u.rol
        WHERE u.email = :email
    """)
    Optional<Usuario> findByEmailWithRol(@Param("email") String email);

}