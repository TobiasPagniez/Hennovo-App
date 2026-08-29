package hennovo_backend.cheques.mapper;

import org.springframework.stereotype.Component;

import hennovo_backend.cheques.dtos.request.ChequeRequest;
import hennovo_backend.cheques.dtos.response.ChequeResponse;
import hennovo_backend.cheques.entitys.Cheque;
import hennovo_backend.clientes.entitys.Cliente;

@Component
public class ChequeMapper {

    public Cheque toEntity(ChequeRequest request, Cliente cliente) {

        Cheque cheque = new Cheque();

        cheque.setFechaIngreso(request.fechaIngreso());
        cheque.setCliente(cliente);
        cheque.setTitular(request.titular());
        cheque.setCodigoBanco(request.codigoBanco());
        cheque.setNombreBanco(request.nombreBanco());
        cheque.setImporte(request.importe());
        cheque.setFechaPago(request.fechaPago());
        cheque.setEndosado(request.endosado());
        cheque.setFirmaTitular(request.firmaTitular());
        cheque.setActivo(true);

        return cheque;
    }

    public void actualizarEntity(Cheque cheque, ChequeRequest request, Cliente cliente) {

        cheque.setFechaIngreso(request.fechaIngreso());
        cheque.setCliente(cliente);
        cheque.setTitular(request.titular());
        cheque.setCodigoBanco(request.codigoBanco());
        cheque.setNombreBanco(request.nombreBanco());
        cheque.setImporte(request.importe());
        cheque.setFechaPago(request.fechaPago());
        cheque.setEndosado(request.endosado());
        cheque.setFirmaTitular(request.firmaTitular());
    }

    public ChequeResponse toResponse(Cheque cheque) {

        return new ChequeResponse(
                cheque.getId(),
                cheque.getFechaIngreso(),
                cheque.getCliente().getId(),
                cheque.getCliente().getNombre(),
                cheque.getTitular(),
                cheque.getCodigoBanco(),
                cheque.getNombreBanco(),
                cheque.getImporte(),
                cheque.getFechaPago(),
                cheque.getEndosado(),
                cheque.getFirmaTitular(),
                cheque.getActivo()
        );
    }
}
