package hennovo_backend.productos.util;

import java.util.EnumSet;
import java.util.Set;

import hennovo_backend.precios.entitys.UnidadPrecio;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.productos.enums.Presentacion;
import hennovo_backend.shared.exception.BadRequestException;

//centralizamos la regla para no repetirla en muchos services
public final class UnidadesPermitidas {

    private UnidadesPermitidas() {
    }

    public static Set<UnidadPrecio> permitidasPara(Producto producto) {

        if (producto.getPresentacion() == Presentacion.CAJITA) {
            return EnumSet.of(UnidadPrecio.CAJITA);
        }

        return EnumSet.of(UnidadPrecio.MAPLE, UnidadPrecio.CAJON);
    }

    public static void validar(Producto producto, UnidadPrecio unidad) {

        if (!permitidasPara(producto).contains(unidad)) {
            throw new BadRequestException(
                    "La unidad " + unidad + " no es válida para este producto (presentación "
                            + producto.getPresentacion()
                            + "). Unidades permitidas: " + permitidasPara(producto));
        }
    }
}
