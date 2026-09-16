package hennovo_backend.clientes.repositorys;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.clientes.entitys.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByCategoriaIdAndActivoTrue(Long categoriaId);
}