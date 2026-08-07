package hennovo_backend.productos.repository;

import hennovo_backend.productos.entity.Producto;
import hennovo_backend.productos.enums.Presentacion;
import hennovo_backend.productos.enums.Tamaño;
import hennovo_backend.productos.enums.TipoHuevo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();

    boolean existsByTipoHuevoAndTamañoAndPresentacion(
            TipoHuevo tipoHuevo,
            Tamaño tamaño,
            Presentacion presentacion
    );
}
