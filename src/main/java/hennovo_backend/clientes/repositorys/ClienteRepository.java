package hennovo_backend.clientes.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.clientes.entitys.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}