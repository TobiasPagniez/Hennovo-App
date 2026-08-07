package hennovo_backend.precios.entitys;

import java.math.BigDecimal;

import hennovo_backend.clientes.entitys.CategoriaCliente;
import hennovo_backend.productos.entity.Producto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "precios_producto",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "producto_id",
                "categoria_id",
                "lista_id"
            }
        )
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrecioProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)    
    private BigDecimal precio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaCliente categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lista_id", nullable = false)
    private ListaPrecio lista;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_precio", nullable = false)
    private UnidadPrecio unidadPrecio;    
}
