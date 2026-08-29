package hennovo_backend.cheques.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.cheques.entitys.Cheque;

public interface ChequeRepository extends JpaRepository<Cheque, Long> {
}
