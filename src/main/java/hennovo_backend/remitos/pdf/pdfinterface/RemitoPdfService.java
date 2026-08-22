package hennovo_backend.remitos.pdf.pdfinterface;

public interface RemitoPdfService {

    byte[] generarPdf(Long remitoId);
}
