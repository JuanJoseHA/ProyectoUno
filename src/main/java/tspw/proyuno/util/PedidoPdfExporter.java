package tspw.proyuno.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import tspw.proyuno.PedidoDetalleRow;
import tspw.proyuno.modelo.Pedido;

public class PedidoPdfExporter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    
    // Fuentes comunes para el ticket
    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
    private static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
    private static final Font FONT_DATA = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    private static final Font FONT_TOTAL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
    private static final Font FONT_FOOTER = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);

    public static ByteArrayOutputStream export(Pedido pedido, List<PedidoDetalleRow> detalles) throws DocumentException {
        // Un documento más pequeño para simular un ticket (ej. ancho de 300 puntos)
        Document document = new Document(new com.itextpdf.text.Rectangle(300, 650)); 
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, outputStream);
        } catch (DocumentException e) {
            throw new RuntimeException("Error al obtener la instancia de PdfWriter", e);
        }

        document.open();

        try {
            // 1. Título y Encabezado del Negocio
            Paragraph businessName = new Paragraph("RESTAURANTE HERATI", FONT_TITLE);
            businessName.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(businessName);
            
            Paragraph address = new Paragraph("Heleodoro Castillo S/N, Zumpango del Rio, Gro.", FONT_HEADER);
            address.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(address);
            
            Paragraph phone = new Paragraph("Tel: (55) 7472217763", FONT_HEADER);
            phone.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(phone);
            
            document.add(Chunk.NEWLINE);

            // 2. Datos del Pedido
            Paragraph ticketTitle = new Paragraph("TICKET DE VENTA", FONT_TOTAL);
            ticketTitle.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(ticketTitle);
            
            Paragraph orderInfo = new Paragraph();
            orderInfo.setAlignment(Paragraph.ALIGN_LEFT);
            orderInfo.add(new Phrase("No. de Pedido: #", FONT_HEADER));
            orderInfo.add(new Phrase(" " + pedido.getIdpedido(), FONT_DATA));
            orderInfo.add(Chunk.NEWLINE);
            
            orderInfo.add(new Phrase("Fecha y Hora:", FONT_HEADER));
            orderInfo.add(new Phrase(" " + pedido.getFecha().format(DATE_FORMAT), FONT_DATA));
            orderInfo.add(Chunk.NEWLINE);

            orderInfo.add(new Phrase("Cliente:", FONT_HEADER));
            orderInfo.add(new Phrase(" " + pedido.getIdcliente().getNombre() + " " + pedido.getIdcliente().getApellidos(), FONT_DATA));
            orderInfo.add(Chunk.NEWLINE);
            
            orderInfo.add(new Phrase("Atendido por:", FONT_HEADER));
            orderInfo.add(new Phrase(" " + pedido.getNombreDeQuienAtiende(), FONT_DATA));
            orderInfo.add(Chunk.NEWLINE);

            if (pedido.getReserva() != null) {
                orderInfo.add(new Phrase("Reservacion: ", FONT_HEADER));
                orderInfo.add(new Phrase("Reserva #" + pedido.getReserva().getIdservicio() + " | Mesa " + pedido.getReserva().getMesa().getIdmesa(), FONT_DATA));
                orderInfo.add(Chunk.NEWLINE);
            }
            
            document.add(orderInfo);
            document.add(Chunk.NEWLINE);

            // 3. Tabla de Detalles (SIN ID de Producto)
            // Se ajustan las columnas a 4: Producto, Cantidad, Precio Unitario, Importe
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            // Se ajustan los anchos para dar más espacio a "Producto"
            table.setWidths(new float[] {4f, 1.5f, 2f, 2f});
            table.setSpacingBefore(5);
            table.setSpacingAfter(5);
            
            Font fontTableHead = FONT_DATA;
            BaseColor headerColor = new BaseColor(230, 230, 230); // Light gray for headers

            // Table Header
            String[] headers = {"Producto", "Cant.", "Precio", "Importe"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontTableHead));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(headerColor);
                cell.setPadding(3);
                cell.setBorder(PdfPCell.BOTTOM);
                table.addCell(cell);
            }
            
            // Table Body
            for (PedidoDetalleRow row : detalles) {
                // Columna 1: Producto (Alineación izquierda)
                table.addCell(createCell(row.getNombreprod(), Element.ALIGN_LEFT, FONT_DATA, PdfPCell.NO_BORDER));
                // Columna 2: Cantidad (Alineación centro)
                table.addCell(createCell(row.getCantidad().toString(), Element.ALIGN_CENTER, FONT_DATA, PdfPCell.NO_BORDER));
                // Columna 3: Precio Unitario (Alineación derecha)
                table.addCell(createCell("$" + DECIMAL_FORMAT.format(row.getPrecio()), Element.ALIGN_RIGHT, FONT_DATA, PdfPCell.NO_BORDER));
                // Columna 4: Importe (Alineación derecha)
                table.addCell(createCell("$" + DECIMAL_FORMAT.format(row.getImporte()), Element.ALIGN_RIGHT, FONT_DATA, PdfPCell.NO_BORDER));
            }
            
            // 4. Separador (Línea)
            PdfPCell separator = new PdfPCell(new Phrase("----------------------------------------------------------------", FONT_FOOTER));
            separator.setColspan(4);
            separator.setHorizontalAlignment(Element.ALIGN_CENTER);
            separator.setBorder(PdfPCell.NO_BORDER);
            separator.setPadding(0);
            table.addCell(separator);
            

            // 5. Fila de TOTAL
            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL:", FONT_TOTAL));
            totalLabel.setColspan(3); // Abarca 3 columnas
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalLabel.setBorder(PdfPCell.NO_BORDER);
            totalLabel.setPaddingTop(5);
            totalLabel.setPaddingBottom(5);
            
            PdfPCell totalValue = new PdfPCell(new Phrase("$" + DECIMAL_FORMAT.format(pedido.getTotal()), FONT_TOTAL));
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue.setBorder(PdfPCell.NO_BORDER);
            totalValue.setPaddingTop(5);
            totalValue.setPaddingBottom(5);
            
            table.addCell(totalLabel);
            table.addCell(totalValue);

            document.add(table);
            
            // 6. Pie de Página/Notas
            document.add(Chunk.NEWLINE);
            Paragraph thankYou = new Paragraph("¡GRACIAS POR SU PREFERENCIA!", FONT_TITLE);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            document.add(thankYou);
            
            document.add(Chunk.NEWLINE);
            Paragraph contact = new Paragraph("Síganos en redes sociales: @juanhander17", FONT_FOOTER);
            contact.setAlignment(Element.ALIGN_CENTER);
            document.add(contact);
            
            Paragraph copyright = new Paragraph("Ticket de venta. ©2025", FONT_FOOTER);
            copyright.setAlignment(Element.ALIGN_CENTER);
            document.add(copyright);

        } catch (DocumentException e) {
            throw new RuntimeException("Error al construir el cuerpo del PDF", e);
        }

        document.close();
        return outputStream;
    }

    private static PdfPCell createCell(String content, int alignment, Font font, int border) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(2);
        cell.setBorder(border); 
        return cell;
    }
}