package hennovo_backend.planillaventas.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import hennovo_backend.planillaventas.dtos.response.PlanillaVentasClienteDTO;

public interface PlanillaVentasService {

    List<PlanillaVentasClienteDTO> obtenerPlanilla(LocalDate fecha);
}
