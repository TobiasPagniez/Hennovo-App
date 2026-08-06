package hennovo_backend.clientes.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.clientes.entitys.CategoriaCliente;

public interface CategoriaClienteRepository extends JpaRepository<CategoriaCliente, Long>{} 
