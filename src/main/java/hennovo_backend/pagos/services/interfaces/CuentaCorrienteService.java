package hennovo_backend.pagos.services.interfaces;

import hennovo_backend.pagos.dtos.response.CuentaCorrienteResponseDTO;

public interface CuentaCorrienteService {

    CuentaCorrienteResponseDTO obtenerPorCliente(Long clienteId);

    void sincronizarEstadoPedidos(Long clienteId);

}
