package hennovo_backend.precios.services.impl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.clientes.entitys.CategoriaCliente;
import hennovo_backend.clientes.repositorys.CategoriaClienteRepository;
import hennovo_backend.precios.dtos.request.ListaPrecioRequest;
import hennovo_backend.precios.dtos.request.PrecioProductoRequest;
import hennovo_backend.precios.dtos.response.ListaPrecioResponse;
import hennovo_backend.precios.dtos.response.PrecioProductoResponse;
import hennovo_backend.precios.entitys.ListaPrecio;
import hennovo_backend.precios.entitys.PrecioProducto;
import hennovo_backend.precios.mapper.ListaPrecioMapper;
import hennovo_backend.precios.mapper.PrecioProductoMapper;
import hennovo_backend.precios.repositorys.ListaPrecioRepository;
import hennovo_backend.precios.repositorys.PrecioProductoRepository;
import hennovo_backend.precios.services.interfaces.ListaPrecioService;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.productos.repository.ProductoRepository;
import hennovo_backend.productos.util.UnidadesPermitidas;
import hennovo_backend.shared.exception.BadRequestException;
import hennovo_backend.shared.exception.ConflictException;
import hennovo_backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListaPrecioServiceImpl implements ListaPrecioService {

    private final ListaPrecioRepository listaPrecioRepository;
    private final PrecioProductoRepository precioProductoRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaClienteRepository categoriaClienteRepository;
    private final ListaPrecioMapper listaPrecioMapper;
    private final PrecioProductoMapper precioProductoMapper;

    @Override
    @Transactional
    public ListaPrecioResponse crear(ListaPrecioRequest request) {
        validarFechas(request);
        cerrarListaVigente(request.fechaDesde());

        ListaPrecio listaPrecio = listaPrecioRepository.save(
                listaPrecioMapper.toEntity(request)
        );

        Set<String> preciosCreados = new HashSet<>();
        for (PrecioProductoRequest precioRequest : request.precios()) {
            String clavePrecio = precioRequest.productoId() + ":" + precioRequest.categoriaId();
            if (!preciosCreados.add(clavePrecio)) {
                throw new ConflictException(
                        "No puede haber dos precios para el mismo producto y categoría"
                );
            }

            Producto producto = productoRepository.findById(precioRequest.productoId())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
            CategoriaCliente categoria = categoriaClienteRepository.findById(precioRequest.categoriaId())
                    .orElseThrow(() -> new NotFoundException("Categoría de cliente no encontrada"));

            UnidadesPermitidas.validar(producto, precioRequest.unidadPrecio());


            PrecioProducto precioProducto = new PrecioProducto();
            precioProducto.setPrecio(precioRequest.precio());
            precioProducto.setUnidadPrecio(precioRequest.unidadPrecio());
            precioProducto.setProducto(producto);
            precioProducto.setCategoria(categoria);
            precioProducto.setLista(listaPrecio);
            precioProductoRepository.save(precioProducto);
        }

        return obtenerPorId(listaPrecio.getId());
    }

    private void validarFechas(ListaPrecioRequest request) {
        if (request.fechaHasta() != null && request.fechaHasta().isBefore(request.fechaDesde())) {
            throw new BadRequestException(
                    "La fecha hasta no puede ser anterior a la fecha desde"
            );
        }
    }

    private void cerrarListaVigente(LocalDate fechaDesdeNuevaLista) {
        listaPrecioRepository.findByFechaHastaIsNull().ifPresent(anterior -> {
            LocalDate nuevaFechaHasta = fechaDesdeNuevaLista.minusDays(1);
            if (nuevaFechaHasta.isBefore(anterior.getFechaDesde())) {
                throw new ConflictException(
                        "La nueva lista de precios debe comenzar después de la lista vigente"
                );
            }
            anterior.setFechaHasta(nuevaFechaHasta);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ListaPrecioResponse obtenerPorId(Long id) {
        ListaPrecio listaPrecio = listaPrecioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lista de precios no encontrada"));

        return listaPrecioMapper.toResponse(listaPrecio, obtenerPrecios(listaPrecio.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListaPrecioResponse> listar() {
        return listaPrecioRepository.findAll().stream()
                .map(listaPrecio -> listaPrecioMapper.toResponse(
                        listaPrecio,
                        obtenerPrecios(listaPrecio.getId())
                ))
                .toList();
    }

    private List<PrecioProductoResponse> obtenerPrecios(Long listaId) {
        return precioProductoRepository.findByListaId(listaId).stream()
                .map(precioProductoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ListaPrecioResponse obtenerVigente() {
        ListaPrecio listaPrecio = listaPrecioRepository.findByFechaHastaIsNull()
                .orElseThrow(() -> new NotFoundException("No existe una lista de precios vigente"));

        return listaPrecioMapper.toResponse(listaPrecio, obtenerPrecios(listaPrecio.getId()));
    }    

}
