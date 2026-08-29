package hennovo_backend.cheques.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hennovo_backend.cheques.dtos.request.ChequeRequest;
import hennovo_backend.cheques.dtos.response.ChequeResponse;
import hennovo_backend.cheques.entitys.Cheque;
import hennovo_backend.cheques.mapper.ChequeMapper;
import hennovo_backend.cheques.repositorys.ChequeRepository;
import hennovo_backend.cheques.services.interfaces.ChequeService;
import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.clientes.repositorys.ClienteRepository;
import hennovo_backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChequeServiceImpl implements ChequeService {

    private final ChequeRepository chequeRepository;
    private final ClienteRepository clienteRepository;
    private final ChequeMapper chequeMapper;

    @Override
    @Transactional
    public ChequeResponse crear(ChequeRequest request) {

        Cliente cliente = obtenerCliente(request.clienteId());

        Cheque cheque = chequeMapper.toEntity(request, cliente);

        return chequeMapper.toResponse(chequeRepository.save(cheque));
    }

    @Override
    @Transactional(readOnly = true)
    public ChequeResponse obtenerPorId(Long id) {

        return chequeMapper.toResponse(obtenerCheque(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChequeResponse> listarTodos() {

        return chequeRepository.findAll()
                .stream()
                .map(chequeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChequeResponse actualizar(Long id, ChequeRequest request) {

        Cheque cheque = obtenerCheque(id);
        Cliente cliente = obtenerCliente(request.clienteId());

        chequeMapper.actualizarEntity(cheque, request, cliente);

        return chequeMapper.toResponse(chequeRepository.save(cheque));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {

        Cheque cheque = obtenerCheque(id);
        cheque.setActivo(false);
        chequeRepository.save(cheque);
    }

    @Override
    @Transactional
    public ChequeResponse reactivar(Long id) {

        Cheque cheque = obtenerCheque(id);
        cheque.setActivo(true);
        return chequeMapper.toResponse(chequeRepository.save(cheque));
    }

    private Cheque obtenerCheque(Long id) {
        return chequeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cheque no encontrado"));
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
    }
}
