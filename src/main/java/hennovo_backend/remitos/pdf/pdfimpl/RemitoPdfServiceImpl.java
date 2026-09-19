package hennovo_backend.remitos.pdf.pdfimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import hennovo_backend.clientes.entitys.Cliente;
import hennovo_backend.pedidos.entitys.Pedido;
import hennovo_backend.productos.entity.Producto;
import hennovo_backend.remitos.entitys.DetalleRemito;
import hennovo_backend.remitos.entitys.Remito;
import hennovo_backend.remitos.pdf.pdfinterface.RemitoPdfService;
import hennovo_backend.remitos.repositorys.DetalleRemitoRepository;
import hennovo_backend.remitos.repositorys.RemitoRepository;
import hennovo_backend.shared.exception.NotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.awt.Color;

@Service
@RequiredArgsConstructor
public class RemitoPdfServiceImpl implements RemitoPdfService {

    private final RemitoRepository remitoRepository;
    private final DetalleRemitoRepository detalleRemitoRepository;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    
    private static final NumberFormat FORMATO_MONEDA = NumberFormat.getNumberInstance(new Locale("es", "AR"));
        
        private static final Color VERDE_HENNOVO =
                new Color(198, 221, 190);

        private static final Color VERDE_CLARO =
                new Color(232, 242, 228);

        private static final Color GRIS_CLARO =
                new Color(245, 245, 245);

        private static final Color GRIS_BORDE =
                new Color(190, 190, 190);        

    @Override
    public byte[] generarPdf(Long remitoId) {

        Remito remito = remitoRepository.findById(remitoId)
                .orElseThrow(() -> new NotFoundException(
                        "Remito no encontrado"));

        List<DetalleRemito> detalles = detalleRemitoRepository.findByRemitoId(remitoId);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Document document = new Document(
                    PageSize.A4,
                    40,
                    40,
                    40,
                    40);

            PdfWriter.getInstance(
                    document,
                    outputStream);

            document.open();

            agregarEncabezado(document, remito);

            agregarDatosCliente(document, remito);

            agregarDetalles(document, detalles);

            agregarTotales(document, remito, detalles);

            agregarPie(document, remito);

            document.close();

            return outputStream.toByteArray();

        } catch (DocumentException | IOException e) {

            throw new RuntimeException(
                    "Error al generar el PDF del remito",
                    e);
        }
    }

    // =========================================================
    // ENCABEZADO
    // =========================================================

    private void agregarEncabezado(
            Document document,
            Remito remito) throws DocumentException, IOException {

        PdfPTable tabla = new PdfPTable(2);

        tabla.setWidthPercentage(100);

        tabla.setWidths(new float[] {
                65,
                35
        });

        Paragraph aviso =
                new Paragraph(
                        "DOCUMENTO NO VÁLIDO COMO FACTURA",
                        new Font(
                                Font.HELVETICA,
                                8,
                                Font.BOLD
                        )
                );

        aviso.setAlignment(Element.ALIGN_CENTER);

        document.add(aviso);

        document.add(new Paragraph(" "));        

        // Logo
        PdfPCell celdaLogo = new PdfPCell();

        celdaLogo.setBorder(Rectangle.NO_BORDER);
        celdaLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);

        try (InputStream inputStream = getClass()
                .getResourceAsStream(
                        "/images/hennovo-logo.jpeg")) { //revisar que tenga el mismo nombre

            if (inputStream != null) {

                byte[] imagenBytes = inputStream.readAllBytes();

                Image logo = Image.getInstance(imagenBytes);

                logo.scaleToFit(100, 70);

                celdaLogo.addElement(logo);
            }
        }

        tabla.addCell(celdaLogo);

        // Título
        PdfPCell celdaTitulo = new PdfPCell();

        celdaTitulo.setBorder(Rectangle.NO_BORDER);
        celdaTitulo.setVerticalAlignment(
                Element.ALIGN_MIDDLE);
        celdaTitulo.setHorizontalAlignment(
                Element.ALIGN_RIGHT);

        Font fuenteTitulo = new Font(
                Font.HELVETICA,
                22,
                Font.BOLD);

        Font fuenteNumero = new Font(
                Font.HELVETICA,
                10);

        Paragraph titulo = new Paragraph(
                "REMITO",
                fuenteTitulo);

        titulo.setAlignment(
                Element.ALIGN_RIGHT);

        celdaTitulo.addElement(titulo);

        Paragraph numero = new Paragraph(
                "N° " + String.format(
                        "%06d",
                        remito.getId()),
                fuenteNumero);

        numero.setAlignment(
                Element.ALIGN_RIGHT);

        celdaTitulo.addElement(numero);

        tabla.addCell(celdaTitulo);

        document.add(tabla);

        // Línea separadora
        PdfPTable separador = new PdfPTable(1);

        separador.setWidthPercentage(100);

        PdfPCell linea = new PdfPCell(
                new Phrase(""));

        linea.setBorder(
                Rectangle.BOTTOM);

        linea.setPadding(0);

        separador.addCell(linea);

        document.add(separador);

        document.add(
                new Paragraph(" "));

        // Fecha
        PdfPTable tablaFecha = new PdfPTable(1);

        tablaFecha.setWidthPercentage(100);

        PdfPCell celdaFecha = new PdfPCell(
                new Phrase(
                        "Fecha: "
                                + remito.getFecha()
                                        .format(FORMATO_FECHA)));

        celdaFecha.setBorder(
                Rectangle.NO_BORDER);

        celdaFecha.setHorizontalAlignment(
                Element.ALIGN_RIGHT);

        tablaFecha.addCell(celdaFecha);

        document.add(tablaFecha);

        document.add(
                new Paragraph(" "));
    }

    // =========================================================
    // CLIENTE
    // =========================================================

    private void agregarDatosCliente(
            Document document,
            Remito remito) throws DocumentException {

        Pedido pedido = remito.getPedido();

        Cliente cliente = pedido.getCliente();

        Font fuenteTitulo = new Font(
                Font.HELVETICA,
                10,
                Font.BOLD);

        Font fuenteNormal = new Font(
                Font.HELVETICA,
                10);

        PdfPTable tabla = new PdfPTable(1);

        tabla.setWidthPercentage(100);

        PdfPCell encabezado = new PdfPCell(
                new Phrase(
                        "DATOS DEL CLIENTE",
                        fuenteTitulo));

        encabezado.setPadding(6);

        tabla.addCell(encabezado);

        encabezado.setBackgroundColor(
                VERDE_HENNOVO
        );

        encabezado.setBorderColor(
                GRIS_BORDE
        );        

        PdfPCell datos = new PdfPCell();

        datos.setPadding(8);

        Paragraph nombre = new Paragraph(
                "Sr./Sres.: "
                        + cliente.getNombre(),
                fuenteNormal);

        datos.addElement(nombre);

        Paragraph direccion = new Paragraph(
                "Dirección: "
                        + cliente.getDireccion(),
                fuenteNormal);

        datos.addElement(direccion);

        Paragraph localidad = new Paragraph(
                "Localidad: "
                        + cliente.getLocalidad(),
                fuenteNormal);

        datos.addElement(localidad);

        tabla.addCell(datos);

        document.add(tabla);

        document.add(
                new Paragraph(" "));
    }

    // =========================================================
    // DETALLES
    // =========================================================

    private void agregarDetalles(
            Document document,
            List<DetalleRemito> detalles)
            throws DocumentException {

        PdfPTable tabla = new PdfPTable(4);

        tabla.setWidthPercentage(100);

        tabla.setWidths(
                new float[] {
                        12,
                        48,
                        20,
                        20
                });

        Font fuenteEncabezado = new Font(
                Font.HELVETICA,
                9,
                Font.BOLD);

        Font fuenteNormal = new Font(
                Font.HELVETICA,
                9);

        agregarCeldaEncabezado(
                tabla,
                "CANT.",
                fuenteEncabezado);

        agregarCeldaEncabezado(
                tabla,
                "DESCRIPCIÓN",
                fuenteEncabezado);

        agregarCeldaEncabezado(
                tabla,
                "PRECIO",
                fuenteEncabezado);

        agregarCeldaEncabezado(
                tabla,
                "IMPORTE",
                fuenteEncabezado);
        
        int fila = 0;
        for (DetalleRemito detalle : detalles) {

            boolean filaPar = fila % 2 == 0;

                Color fondo =
                        filaPar
                                ? Color.WHITE
                                : GRIS_CLARO;            

                agregarCelda(
                        tabla,
                        String.valueOf(detalle.getCantidad()),
                        fuenteNormal,
                        Element.ALIGN_CENTER,
                        fondo
                );

                agregarCelda(
                        tabla,
                        obtenerDescripcion(detalle.getProducto()),
                        fuenteNormal,
                        Element.ALIGN_LEFT,
                        fondo
                );

                agregarCelda(
                        tabla,
                        "$ " + formatearMoneda(
                                detalle.getPrecioUnitario()
                        ),
                        fuenteNormal,
                        Element.ALIGN_RIGHT,
                        fondo
                );

                agregarCelda(
                        tabla,
                        "$ " + formatearMoneda(
                                detalle.getImporte()
                        ),
                        fuenteNormal,
                        Element.ALIGN_RIGHT,
                        fondo
                );

                fila++;
        }

        document.add(tabla);

        document.add(
                new Paragraph(" "));
        
        
    }

    // =========================================================
    // TOTALES
    // =========================================================

    private void agregarTotales(
            Document document,
            Remito remito,
            List<DetalleRemito> detalles)
            throws DocumentException {

        BigDecimal total = detalles.stream()
                .map(DetalleRemito::getImporte)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);

        PdfPTable tabla = new PdfPTable(2);

        tabla.setWidthPercentage(100);

        tabla.setWidths(
                new float[] {
                        55,
                        45
                });

        // Facturación
        PdfPCell facturacion = new PdfPCell(
                new Phrase(
                        "Corresponde facturación: "
                                + (Boolean.TRUE.equals(
                                        remito.getCorrespondeFacturacion())
                                                ? "SI"
                                                : "NO")));

        facturacion.setBorder(
                Rectangle.NO_BORDER);

        facturacion.setVerticalAlignment(
                Element.ALIGN_MIDDLE);

        tabla.addCell(facturacion);

        // Total
        PdfPCell totalCelda =
                new PdfPCell();

        totalCelda.setBorder(
                Rectangle.NO_BORDER
        );

        totalCelda.setBackgroundColor(
                VERDE_HENNOVO
        );

        totalCelda.setPadding(8);

        totalCelda.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );

        Font fuenteTotal =
                new Font(
                        Font.HELVETICA,
                        14,
                        Font.BOLD
                );

        totalCelda.addElement(
                new Paragraph(
                        "TOTAL",
                        fuenteTotal
                )
        );

        Paragraph importeTotal =
                new Paragraph(
                        "$ " + formatearMoneda(total),
                        fuenteTotal
                );

        importeTotal.setAlignment(
                Element.ALIGN_RIGHT
        );

        totalCelda.addElement(
                importeTotal
        );

        tabla.addCell(totalCelda);

        document.add(tabla);
    }

    // =========================================================
    // PIE
    // =========================================================

    private void agregarPie(
            Document document,
            Remito remito)
            throws DocumentException {

        document.add(new Paragraph(" "));

        Paragraph pie = new Paragraph(
                "HENNOVO",
                new Font(Font.HELVETICA, 8));

        pie.setAlignment(Element.ALIGN_CENTER);

        document.add(pie);
    }

    // =========================================================
    // MÉTODOS AUXILIARES
    // =========================================================

        private void agregarCeldaEncabezado(
                PdfPTable tabla,
                String texto,
                Font fuente) {

        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto,
                                fuente
                        )
                );

        celda.setBackgroundColor(
                VERDE_HENNOVO
        );

        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        celda.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        celda.setPadding(7);

        celda.setBorderColor(
                GRIS_BORDE
        );

        tabla.addCell(celda);
        }

        private void agregarCelda(
                PdfPTable tabla,
                String texto,
                Font fuente,
                int alineacion,
                Color fondo) {

        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto,
                                fuente
                        )
                );

        celda.setBackgroundColor(fondo);

        celda.setHorizontalAlignment(
                alineacion
        );

        celda.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        celda.setPadding(6);

        celda.setBorderColor(
                GRIS_BORDE
        );

        tabla.addCell(celda);
        }

    private String obtenerDescripcion(Producto producto) {

        String tipo = switch (producto.getTipoHuevo()) {
            case BLANCO -> "Blanco";
            case COLOR -> "Color";
        };

        String tamaño = switch (producto.getTamaño()) {
            case GRANDE -> "1";
            case MEDIANO -> "2";
            case CHICO -> "3";
            case CHICO_4 -> "4";
            case BOLITA -> "Bolita";
            case SUPER -> "Super";
        };

        String presentacion = switch (producto.getPresentacion()) {
            case MAPLE -> "";
            case CAJON -> "Cajón";
            case CAJITA -> "Cajita";
            case CAJON_DE_CAJITAS -> "Cajón de cajitas";
        };

        if (presentacion.isBlank()) {
            return tipo + " " + tamaño;
        }

        return presentacion + " " + tipo.toLowerCase() + " " + tamaño;
    }

    private String formatearMoneda(
            BigDecimal valor) {

        return FORMATO_MONEDA.format(valor);
    }
}
