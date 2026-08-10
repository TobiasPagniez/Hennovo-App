package hennovo_backend.precios.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hennovo_backend.precios.entitys.PrecioProducto;


public interface PrecioProductoRepository extends JpaRepository<PrecioProducto, Long> {

    boolean existsByProductoIdAndCategoriaIdAndListaId(
            Long productoId,
            Long categoriaId,
            Long listaId
    );

    List<PrecioProducto> findByListaId(Long listaId);

    Optional<PrecioProducto> findByProductoIdAndCategoriaIdAndListaId(
            Long productoId,
            Long categoriaId,
            Long listaId
    );
}
