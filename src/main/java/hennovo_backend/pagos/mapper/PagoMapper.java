package hennovo_backend.pagos.mapper;

import org.springframework.stereotype.Component;

import hennovo_backend.pagos.dtos.request.PagoRequestDTO;
import hennovo_backend.pagos.dtos.response.PagoResponseDTO;
import hennovo_backend.pagos.entitys.Pago;

@Component
public class PagoMapper {

    public Pago toEntity(PagoRequestDTO dto) {

        Pago pago = new Pago();

        pago.setImporte(dto.importe());
        pago.setMedioPago(dto.medioPago());
        pago.setNumeroComprobante(dto.numeroComprobante());
        pago.setObservaciones(dto.observaciones());

        return pago;
    }

    public PagoResponseDTO toResponseDTO(Pago pago) {

        return new PagoResponseDTO(
                pago.getId(),
                pago.getFecha(),
                pago.getImporte(),
                pago.getMedioPago(),
                pago.getNumeroComprobante(),
                pago.getObservaciones(),
                pago.getAnulado(),
                pago.getCliente().getId()
        );
    }
}
