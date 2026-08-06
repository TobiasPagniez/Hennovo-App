package hennovo_backend.productos.dtos;

import hennovo_backend.productos.enums.Presentacion;
import hennovo_backend.productos.enums.Tamaño;
import hennovo_backend.productos.enums.TipoHuevo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponse {
    private Long id;
    private TipoHuevo tipoHuevo;
    private Tamaño tamaño;
    private Presentacion presentacion;
    private Boolean activo;
}
