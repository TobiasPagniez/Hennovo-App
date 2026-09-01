package hennovo_backend.plantillacarga.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.plantillacarga.dtos.request.ActualizarCantidadDetalleRequest;
import hennovo_backend.plantillacarga.dtos.request.AgregarDetalleCeldaRequest;
import hennovo_backend.plantillacarga.dtos.request.ConfigurarCroquisRequest;
import hennovo_backend.plantillacarga.dtos.request.CopiarCroquisRequest;
import hennovo_backend.plantillacarga.dtos.request.MoverDetalleCeldaRequest;
import hennovo_backend.plantillacarga.dtos.response.PlantillaCargaResponse;
import hennovo_backend.plantillacarga.entitys.CeldaPlantilla;
import hennovo_backend.plantillacarga.entitys.DetalleCelda;
import hennovo_backend.plantillacarga.entitys.NivelCarga;
import hennovo_backend.plantillacarga.entitys.PlantillaCarga;
import hennovo_backend.plantillacarga.mapper.PlantillaCargaMapper;
import hennovo_backend.plantillacarga.repositorys.CeldaPlantillaRepository;
import hennovo_backend.plantillacarga.repositorys.DetalleCeldaRepository;
import hennovo_backend.plantillacarga.repositorys.PlantillaCargaRepository;
import hennovo_backend.plantillacarga.services.interfaces.PlantillaCargaService;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.productos.repository.ProductoRepository;
import hennovo_backend.shared.exception.NotFoundException;
import hennovo_backend.vehiculos.entitys.Vehiculo;
import hennovo_backend.vehiculos.repositorys.VehiculoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlantillaCargaServiceImpl implements PlantillaCargaService {

    private final PlantillaCargaRepository plantillaCargaRepository;
    private final CeldaPlantillaRepository celdaPlantillaRepository;
    private final DetalleCeldaRepository detalleCeldaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ProductoRepository productoRepository;
    private final PlantillaCargaMapper plantillaCargaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PlantillaCargaResponse> obtenerPorVehiculo(Long vehiculoId, LocalDate fecha) {

        obtenerVehiculo(vehiculoId);

        return plantillaCargaRepository.findByVehiculoIdOrderByNivelAsc(vehiculoId)
                .stream()
                .map(plantilla -> plantillaCargaMapper.toResponse(plantilla, fecha))
                .toList();
    }

    @Override
    @Transactional
    public List<PlantillaCargaResponse> configurar(Long vehiculoId, ConfigurarCroquisRequest request) {

        Vehiculo vehiculo = obtenerVehiculo(vehiculoId);

        upsertPlantilla(vehiculo, NivelCarga.INFERIOR, "Base", request.filas(), request.columnas());

        if (Boolean.TRUE.equals(request.incluirNivelSuperior())) {
            upsertPlantilla(vehiculo, NivelCarga.SUPERIOR, "Superior", request.filas(), request.columnas());
        }

        // Al reconfigurar, no hay contenido para ninguna fecha todavía, así que
        // devolver la vista de "hoy" es equivalente a cualquier otra fecha.
        return obtenerPorVehiculo(vehiculoId, LocalDate.now());
    }

    private void upsertPlantilla(
            Vehiculo vehiculo,
            NivelCarga nivel,
            String nombrePorDefecto,
            Integer filas,
            Integer columnas) {

        Optional<PlantillaCarga> existente = plantillaCargaRepository
                .findByVehiculoIdAndNivel(vehiculo.getId(), nivel);

        if (existente.isEmpty()) {

            PlantillaCarga plantilla = new PlantillaCarga();
            plantilla.setNombre(nombrePorDefecto);
            plantilla.setFilas(filas);
            plantilla.setColumnas(columnas);
            plantilla.setNivel(nivel);
            plantilla.setVehiculo(vehiculo);

            generarCeldas(plantilla, filas, columnas);

            plantillaCargaRepository.save(plantilla);
            return;
        }

        PlantillaCarga plantilla = existente.get();

        boolean cambioDeTamano =
                !filas.equals(plantilla.getFilas()) || !columnas.equals(plantilla.getColumnas());

        if (cambioDeTamano) {
            // Esto borra el contenido de TODAS las fechas cargadas para este
            // vehículo y nivel, no solo la fecha que se esté viendo.
            plantilla.getCeldas().clear();
            plantilla.setFilas(filas);
            plantilla.setColumnas(columnas);
            generarCeldas(plantilla, filas, columnas);
            plantillaCargaRepository.save(plantilla);
        }
    }

    private void generarCeldas(PlantillaCarga plantilla, Integer filas, Integer columnas) {

        for (int fila = 1; fila <= filas; fila++) {
            for (int columna = 1; columna <= columnas; columna++) {

                CeldaPlantilla celda = new CeldaPlantilla();
                celda.setFila(fila);
                celda.setColumna(columna);
                celda.setPlantilla(plantilla);

                plantilla.getCeldas().add(celda);
            }
        }
    }

    @Override
    @Transactional
    public void agregarDetalle(Long celdaId, AgregarDetalleCeldaRequest request) {

        CeldaPlantilla celda = celdaPlantillaRepository.findById(celdaId)
                .orElseThrow(() -> new NotFoundException("Celda no encontrada"));

        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        DetalleCelda detalle = new DetalleCelda();
        detalle.setCelda(celda);
        detalle.setProducto(producto);
        detalle.setCantidad(request.cantidad());
        detalle.setFecha(request.fecha());

        detalleCeldaRepository.save(detalle);
    }

    @Override
    @Transactional
    public void moverDetalle(Long detalleId, MoverDetalleCeldaRequest request) {

        DetalleCelda detalle = obtenerDetalle(detalleId);

        CeldaPlantilla celdaDestino = celdaPlantillaRepository.findById(request.celdaDestinoId())
                .orElseThrow(() -> new NotFoundException("Celda de destino no encontrada"));

        detalle.setCelda(celdaDestino);

        detalleCeldaRepository.save(detalle);
    }

    @Override
    @Transactional
    public void actualizarCantidad(Long detalleId, ActualizarCantidadDetalleRequest request) {

        DetalleCelda detalle = obtenerDetalle(detalleId);

        detalle.setCantidad(request.cantidad());

        detalleCeldaRepository.save(detalle);
    }

    @Override
    @Transactional
    public void eliminarDetalle(Long detalleId) {

        DetalleCelda detalle = obtenerDetalle(detalleId);

        detalleCeldaRepository.delete(detalle);
    }

    @Override
    @Transactional
    public void copiarDia(Long vehiculoId, CopiarCroquisRequest request) {

        Vehiculo vehiculo = obtenerVehiculo(vehiculoId);

        List<PlantillaCarga> plantillas = plantillaCargaRepository
                .findByVehiculoIdOrderByNivelAsc(vehiculo.getId());

        for (PlantillaCarga plantilla : plantillas) {
            for (CeldaPlantilla celda : plantilla.getCeldas()) {

                List<DetalleCelda> paraBorrar = celda.getDetalles().stream()
                        .filter(d -> d.getFecha().equals(request.fechaDestino()))
                        .toList();

                detalleCeldaRepository.deleteAll(paraBorrar);

                List<DetalleCelda> paraCopiar = celda.getDetalles().stream()
                        .filter(d -> d.getFecha().equals(request.fechaOrigen()))
                        .toList();

                List<DetalleCelda> nuevos = new ArrayList<>();
                for (DetalleCelda origen : paraCopiar) {

                    DetalleCelda copia = new DetalleCelda();
                    copia.setCelda(celda);
                    copia.setProducto(origen.getProducto());
                    copia.setCantidad(origen.getCantidad());
                    copia.setFecha(request.fechaDestino());

                    nuevos.add(copia);
                }

                detalleCeldaRepository.saveAll(nuevos);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> obtenerFechasConContenido(Long vehiculoId) {

        obtenerVehiculo(vehiculoId);

        return detalleCeldaRepository.findFechasDistintasByVehiculoId(vehiculoId);
    }

    private DetalleCelda obtenerDetalle(Long id) {
        return detalleCeldaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El contenido de celda no existe"));
    }

    private Vehiculo obtenerVehiculo(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehículo no encontrado"));
    }
}
