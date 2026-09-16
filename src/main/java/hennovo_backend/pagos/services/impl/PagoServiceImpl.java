package hennovo_backend.pagos.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.cheques.entitys.Cheque;
import hennovo_backend.cheques.mapper.ChequeMapper;
import hennovo_backend.cheques.repositorys.ChequeRepository;
import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.clientes.repositorys.ClienteRepository;
import hennovo_backend.pagos.dtos.request.PagoRequestDTO;
import hennovo_backend.pagos.dtos.response.PagoResponseDTO;
import hennovo_backend.pagos.entitys.MedioPago;
import hennovo_backend.pagos.entitys.Pago;
import hennovo_backend.pagos.mapper.PagoMapper;
import hennovo_backend.pagos.repositorys.PagoRepository;
import hennovo_backend.pagos.services.interfaces.CuentaCorrienteService;
import hennovo_backend.pagos.services.interfaces.PagoService;
import hennovo_backend.shared.exception.BadRequestException;
import hennovo_backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ClienteRepository clienteRepository;
    private final PagoMapper pagoMapper;
    private final ChequeRepository chequeRepository;
    private final ChequeMapper chequeMapper;
    private final CuentaCorrienteService cuentaCorrienteService;

    @Override
    public PagoResponseDTO crear(PagoRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        if (!cliente.getActivo()) {
            throw new BadRequestException(
                    "No se puede registrar un pago para un cliente inactivo");
        }

        // Crear el pago
        Pago pago = pagoMapper.toEntity(dto);

        pago.setCliente(cliente);
        pago.setFecha(LocalDate.now());
        pago.setAnulado(false);

        Pago pagoGuardado = pagoRepository.save(pago);

        if (dto.medioPago() == MedioPago.CHEQUE) {

            validarDatosCheque(dto);
            //esto va en el mapper
            Cheque cheque = new Cheque();

            cheque.setFechaIngreso(LocalDate.now());
            cheque.setCliente(cliente);
            cheque.setTitular(dto.titular());
            cheque.setCodigoBanco(dto.codigoBanco());
            cheque.setNombreBanco(dto.nombreBanco());
            cheque.setImporte(dto.importe());
            cheque.setFechaPago(dto.fechaPago());
            cheque.setEndosado(dto.endosado());
            cheque.setFirmaTitular(dto.firmaTitular());
            cheque.setActivo(true);

            chequeRepository.save(cheque);
        }

        return pagoMapper.toResponseDTO(pagoGuardado);
    }

    private void validarDatosCheque(PagoRequestDTO dto) {

        if (dto.titular() == null || dto.titular().isBlank()) {
            throw new BadRequestException(
                    "El titular es obligatorio para pagos con cheque");
        }

        if (dto.codigoBanco() == null || dto.codigoBanco().isBlank()) {
            throw new BadRequestException(
                    "El código de banco es obligatorio para pagos con cheque");
        }

        if (dto.nombreBanco() == null || dto.nombreBanco().isBlank()) {
            throw new BadRequestException(
                    "El nombre del banco es obligatorio para pagos con cheque");
        }

        if (dto.fechaPago() == null) {
            throw new BadRequestException(
                    "La fecha de pago es obligatoria para pagos con cheque");
        }

        if (dto.endosado() == null) {
            throw new BadRequestException(
                    "Debe indicar si el cheque está endosado");
        }

        if (dto.firmaTitular() == null) {
            throw new BadRequestException(
                    "Debe indicar si el cheque tiene firma del titular");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPorId(Long id) {

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado"));

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
            throw new NotFoundException(
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
                .orElseThrow(() -> new NotFoundException("Pago no encontrado"));

        if (pago.getAnulado()) {
            throw new BadRequestException("El pago ya se encuentra anulado");
        }

        pago.setAnulado(true);
        pagoRepository.save(pago);

        // Sincroniza Pedido.pagado: pedidos que estaban PAGADO pueden volver
        // a PARCIAL/PENDIENTE al anularse el pago que los cubría
        cuentaCorrienteService.sincronizarEstadoPedidos(pago.getCliente().getId());
    }
}
