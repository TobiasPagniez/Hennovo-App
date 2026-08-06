package hennovo_backend.productos.dtos;

import hennovo_backend.productos.enums.Presentacion;
import hennovo_backend.productos.enums.Tamaño;
import hennovo_backend.productos.enums.TipoHuevo;

public class ProductoRequest {
    private TipoHuevo tipoHuevo;
    private Tamaño tamaño;
    private Presentacion presentacion;
}
