package hennovo_backend.precios.repositorys;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import hennovo_backend.precios.entitys.ListaPrecio;


public interface ListaPrecioRepository extends JpaRepository<ListaPrecio, Long> {
    Optional<ListaPrecio> findByFechaHastaIsNull();
}
