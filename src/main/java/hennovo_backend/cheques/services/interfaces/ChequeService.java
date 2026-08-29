package hennovo_backend.cheques.services.interfaces;

import java.util.List;

import hennovo_backend.cheques.dtos.request.ChequeRequest;
import hennovo_backend.cheques.dtos.response.ChequeResponse;

public interface ChequeService {

    ChequeResponse crear(ChequeRequest request);

    ChequeResponse obtenerPorId(Long id);

    List<ChequeResponse> listarTodos();

    ChequeResponse actualizar(Long id, ChequeRequest request);

    void desactivar(Long id);

    ChequeResponse reactivar(Long id);
}
