package hennovo_backend.productos.entity;

import hennovo_backend.productos.enums.Presentacion;
import hennovo_backend.productos.enums.Tamaño;
import hennovo_backend.productos.enums.TipoHuevo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHuevo tipoHuevo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tamaño tamaño;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Presentacion presentacion;

    @Column(nullable = false)
    private Boolean activo = true;
}