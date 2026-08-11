package hennovo_backend.rutas.mapper;

import org.springframework.stereotype.Component;

import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.rutas.dtos.request.RutaRequest;
import hennovo_backend.rutas.dtos.response.RutaResponse;
import hennovo_backend.rutas.entitys.Ruta;
import hennovo_backend.vehiculos.entitys.Vehiculo;

@Component
public class RutaMapper {

    public Ruta toEntity(
            RutaRequest request,
            Usuario usuario,
            Vehiculo vehiculo
    ) {
        Ruta ruta = new Ruta();

        ruta.setFecha(request.fecha());
        ruta.setNombre(request.nombre());
        ruta.setObservaciones(request.observaciones());
        ruta.setUsuario(usuario);
        ruta.setVehiculo(vehiculo);
        ruta.setActiva(true);

        return ruta;
    }

    public RutaResponse toResponse(Ruta ruta) {
        return new RutaResponse(
                ruta.getId(),
                ruta.getFecha(),
                ruta.getNombre(),
                ruta.getObservaciones(),
                ruta.getActiva(),
                ruta.getUsuario().getId(),
                ruta.getVehiculo().getId()
        );
    }
}
