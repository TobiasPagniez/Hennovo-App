package hennovo_backend.rutas.services.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.auth.entitys.NombreRol;
import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.auth.repositorys.UsuarioRepository;
import hennovo_backend.pedidos.entitys.Pedido;
import hennovo_backend.pedidos.repositorys.PedidoRepository;
import hennovo_backend.rutas.dtos.request.AsignarPedidosRequest;
import hennovo_backend.rutas.dtos.request.OrdenPedidoRequest;
import hennovo_backend.rutas.dtos.request.RutaRequest;
import hennovo_backend.rutas.dtos.response.RutaPedidoResponse;
import hennovo_backend.rutas.dtos.response.RutaResponse;
import hennovo_backend.rutas.entitys.Ruta;
import hennovo_backend.rutas.mapper.RutaMapper;
import hennovo_backend.rutas.repositorys.RutaRepository;
import hennovo_backend.rutas.services.interfaces.RutaService;
import hennovo_backend.shared.exception.BadRequestException;
import hennovo_backend.shared.exception.NotFoundException;
import hennovo_backend.vehiculos.entitys.Vehiculo;
import hennovo_backend.vehiculos.repositorys.VehiculoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RutaServiceImpl implements RutaService {

    private final RutaRepository rutaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PedidoRepository pedidoRepository;

    private final RutaMapper rutaMapper;

    @Override
    public RutaResponse crear(RutaRequest request) {

        Usuario usuario = obtenerUsuarioActivo(request.usuarioId());

        Vehiculo vehiculo = obtenerVehiculoActivo(request.vehiculoId());

        Ruta ruta = rutaMapper.toEntity(
                request,
                usuario,
                vehiculo);

        ruta = rutaRepository.save(ruta);

        return rutaMapper.toResponse(ruta);
    }

    @Override
    @Transactional(readOnly = true)
    public RutaResponse obtenerPorId(Long id) {

        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        return rutaMapper.toResponse(ruta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutaResponse> listar() {

        return rutaRepository.findAll()
                .stream()
                .map(rutaMapper::toResponse)
                .toList();
    }

    @Override
    public RutaResponse actualizar(
            Long id,
            RutaRequest request) {

        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        if (!ruta.getActiva()) {
            throw new BadRequestException(
                    "No se puede modificar una ruta inactiva");
        }

        Usuario usuario = obtenerUsuarioActivo(request.usuarioId());

        Vehiculo vehiculo = obtenerVehiculoActivo(request.vehiculoId());

        ruta.setFecha(request.fecha());
        ruta.setNombre(request.nombre());
        ruta.setObservaciones(request.observaciones());
        ruta.setUsuario(usuario);
        ruta.setVehiculo(vehiculo);

        ruta = rutaRepository.save(ruta);

        return rutaMapper.toResponse(ruta);
    }

    @Override
    public void desactivar(Long id) {

        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        ruta.setActiva(false);

        rutaRepository.save(ruta);
    }

    private Usuario obtenerUsuarioActivo(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException(
                        "Empleado no encontrado"));

        if (!usuario.getActivo()) {
            throw new BadRequestException(
                    "El empleado está inactivo");
        }

        if (usuario.getRol().getNombre() != NombreRol.EMPLEADO) {
            throw new BadRequestException(
                    "La ruta debe estar asignada a un empleado");
        }

        return usuario;
    }

    private Vehiculo obtenerVehiculoActivo(Long vehiculoId) {

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new NotFoundException(
                        "Vehículo no encontrado"));

        if (!vehiculo.getActivo()) {
            throw new BadRequestException(
                    "El vehículo está inactivo");
        }

        return vehiculo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutaPedidoResponse> listarPedidos(Long rutaId) {

        rutaRepository.findById(rutaId)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        return pedidoRepository
                .findByRutaIdOrderByOrdenRutaAsc(rutaId)
                .stream()
                .map(this::toRutaPedidoResponse)
                .toList();
    }

    private RutaPedidoResponse toRutaPedidoResponse(Pedido pedido) {

        return new RutaPedidoResponse(
                pedido.getId(),
                pedido.getOrdenRuta(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNombre(),
                pedido.getCliente().getDireccion(),
                pedido.getEntregado());
    }

    @Override
    public List<RutaPedidoResponse> asignarPedidos(
            Long rutaId,
            AsignarPedidosRequest request) {

        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        if (!ruta.getActiva()) {
            throw new BadRequestException(
                    "No se pueden modificar los pedidos de una ruta inactiva");
        }
        Set<Integer> ordenes = request.pedidos()
                .stream()
                .map(OrdenPedidoRequest::orden)
                .collect(Collectors.toSet());

        if (ordenes.size() != request.pedidos().size()) {
            throw new BadRequestException(
                    "No puede haber pedidos con el mismo orden");
        }

        for (OrdenPedidoRequest item : request.pedidos()) {

            Pedido pedido = pedidoRepository.findById(item.pedidoId())
                    .orElseThrow(() -> new NotFoundException(
                            "Pedido no encontrado: "
                                    + item.pedidoId()));

            if (!pedido.getFecha().equals(ruta.getFecha())) {
                throw new BadRequestException(
                        "El pedido " + pedido.getId()
                                + " no corresponde a la fecha de la ruta");
            }

            if (pedido.getRuta() != null
                    && !pedido.getRuta().getId().equals(rutaId)) {

                throw new BadRequestException(
                        "El pedido " + pedido.getId()
                                + " ya pertenece a otra ruta");
            }

            pedido.setRuta(ruta);
            pedido.setOrdenRuta(item.orden());

            pedidoRepository.save(pedido);
        }

        return listarPedidos(rutaId);
    }

    @Override
    public void quitarPedido(Long rutaId, Long pedidoId) {

        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        if (!ruta.getActiva()) {
            throw new BadRequestException(
                    "No se pueden modificar los pedidos de una ruta inactiva");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        if (pedido.getRuta() == null
                || !pedido.getRuta().getId().equals(rutaId)) {

            throw new BadRequestException(
                    "El pedido no pertenece a esta ruta");
        }

        pedido.setRuta(null);
        pedido.setOrdenRuta(null);

        pedidoRepository.save(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutaPedidoResponse> listarPedidosDisponibles(LocalDate fecha) {

        return pedidoRepository
                .findByFechaAndRutaIsNull(fecha)
                .stream()
                .map(this::toRutaPedidoResponse)
                .toList();
    }
}
