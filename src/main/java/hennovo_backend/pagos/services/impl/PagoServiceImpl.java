package hennovo_backend.pagos.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.clientes.repositorys.ClienteRepository;
import hennovo_backend.pagos.dtos.request.PagoRequestDTO;
import hennovo_backend.pagos.dtos.response.PagoResponseDTO;
import hennovo_backend.pagos.entitys.Pago;
import hennovo_backend.pagos.mapper.PagoMapper;
import hennovo_backend.pagos.repositorys.PagoRepository;
import hennovo_backend.pagos.services.interfaces.PagoService;
import hennovo_backend.shared.exception.BadRequestException;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ClienteRepository clienteRepository;
    private final PagoMapper pagoMapper;

    @Override
    public PagoResponseDTO crear(PagoRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Cliente no encontrado"));

        if (!cliente.getActivo()) {
            throw new BadRequestException(
                    "No se puede registrar un pago para un cliente inactivo");
        }

        Pago pago = pagoMapper.toEntity(dto);

        pago.setCliente(cliente);
        pago.setFecha(LocalDate.now());
        pago.setAnulado(false);

        Pago pagoGuardado = pagoRepository.save(pago);

        return pagoMapper.toResponseDTO(pagoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPorId(Long id) {

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pago no encontrado"));

        return pagoMapper.toResponseDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> obtenerTodos() {

        return pagoRepository.findAll()
                .stream()
                .map(pagoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> obtenerPorCliente(Long clienteId) {

        if (!clienteRepository.existsById(clienteId)) {
            throw new EntityNotFoundException(
                    "Cliente no encontrado");
        }

        return pagoRepository
                .findByClienteIdOrderByFechaAsc(clienteId)
                .stream()
                .map(pagoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void anular(Long id) {

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pago no encontrado"));

        if (pago.getAnulado()) {
            throw new BadRequestException(
                    "El pago ya se encuentra anulado");
        }

        pago.setAnulado(true);

        pagoRepository.save(pago);
    }
}