package com.hospedajesierra.hospedaje_sierra_backend.controller;

import com.hospedajesierra.hospedaje_sierra_backend.dto.InformeDiaDto;
import com.hospedajesierra.hospedaje_sierra_backend.entity.Compra;
import com.hospedajesierra.hospedaje_sierra_backend.entity.FacturaReserva;
import com.hospedajesierra.hospedaje_sierra_backend.entity.Producto;
import com.hospedajesierra.hospedaje_sierra_backend.entity.Reserva;
import com.hospedajesierra.hospedaje_sierra_backend.repository.CompraRepository;
import com.hospedajesierra.hospedaje_sierra_backend.repository.FacturaReservaRepository;
import com.hospedajesierra.hospedaje_sierra_backend.repository.ProductoRepository;
import com.hospedajesierra.hospedaje_sierra_backend.repository.ReservaRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// Define el controlador REST para reportes
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReservaRepository reservaRepo;
    private final CompraRepository compraRepo;
    private final FacturaReservaRepository facturaRepo;
    private final ProductoRepository productoRepo;

    // Genera informe del día
    @GetMapping("/dia")
    public ResponseEntity<List<InformeDiaDto>> getInformeDia() {
        LocalDate hoy = LocalDate.now();

        // Busca reservas vigentes
        List<Reserva> vigentes = reservaRepo.findByFechaEntradaLessThanEqualAndFechaSalidaGreaterThanEqual(hoy, hoy);

        // Calcula totales por reserva
        List<InformeDiaDto> informe = vigentes.stream().map(reserva -> {
            Compra compra = compraRepo.findByReservaIdReserva(reserva.getIdReserva()).orElse(null);
            FacturaReserva factura = facturaRepo.findByReservaIdReserva(reserva.getIdReserva()).orElse(null);

            double servicios = compra != null ? compra.getTotalCompra() : 0.0;
            double habitacion = reserva.getPrecioTotalHabitacion() != null ? reserva.getPrecioTotalHabitacion() : 0.0;
            double total = servicios + habitacion;

            return new InformeDiaDto(
                    reserva.getIdReserva(),
                    reserva.getHuesped().getNombres() + " " + reserva.getHuesped().getApellidos(),
                    reserva.getHuesped().getCedula(),
                    reserva.getHabitacion().getNumero(),
                    reserva.getFechaEntrada().toString(),
                    reserva.getFechaSalida().toString(),
                    servicios,
                    habitacion,
                    total,
                    compra != null ? compra.getIdCompra() : null
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(informe);
    }

    // Genera PDF de lista de productos
    @GetMapping("/pdf/productos")
    public ResponseEntity<byte[]> generarPdfProductos() throws Exception {
        List<Producto> productos = productoRepo.findByActivoTrue();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 70, 50);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Agrega título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(20, 41, 82));
        Paragraph title = new Paragraph("Lista de Productos - Hospedaje Sierra", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Agrega fecha
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);
        Paragraph date = new Paragraph("Generado el: " + LocalDate.now(), dateFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(30);
        document.add(date);

        // Crea tabla
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell header1 = new PdfPCell(new Phrase("Producto", headerFont));
        header1.setBackgroundColor(new Color(20, 41, 82));
        header1.setPadding(10);
        header1.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell header2 = new PdfPCell(new Phrase("Precio (COP)", headerFont));
        header2.setBackgroundColor(new Color(20, 41, 82));
        header2.setPadding(10);
        header2.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(header1);
        table.addCell(header2);

        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        boolean alternate = false;

        // Agrega filas de productos
        for (Producto p : productos) {
            PdfPCell cellName = new PdfPCell(new Phrase(p.getNombre(), cellFont));
            cellName.setPadding(8);
            cellName.setBorderWidth(0.5f);
            cellName.setBackgroundColor(alternate ? new Color(245, 245, 245) : Color.WHITE);

            PdfPCell cellPrice = new PdfPCell(new Phrase("$" + String.format("%,.0f", p.getPrecio()), cellFont));
            cellPrice.setPadding(8);
            cellPrice.setBorderWidth(0.5f);
            cellPrice.setBackgroundColor(alternate ? new Color(245, 245, 245) : Color.WHITE);
            cellPrice.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(cellName);
            table.addCell(cellPrice);
            alternate = !alternate;
        }

        document.add(table);
        document.close();

        byte[] pdfBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "productos.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // Genera PDF de ocupación mensual
    @GetMapping("/pdf/ocupacion")
    public ResponseEntity<byte[]> generarPdfOcupacion(@RequestParam String mesAnio) throws Exception {
        YearMonth yearMonth = YearMonth.parse(mesAnio);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        int diasMes = yearMonth.lengthOfMonth();

        // Agrupa reservas por habitación
        List<Reserva> reservasMes = reservaRepo.findAll().stream()
                .filter(r -> {
                    LocalDate ini = r.getFechaEntrada();
                    LocalDate fin = r.getFechaSalida();
                    return !ini.isAfter(end) && !fin.isBefore(start);
                })
                .collect(Collectors.toList());

        Map<String, List<Reserva>> porHabitacion = reservasMes.stream()
                .collect(Collectors.groupingBy(r -> r.getHabitacion().getNumero()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 70, 50);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Agrega título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(20, 41, 82));
        Paragraph title = new Paragraph("Informe de Ocupación - " + mesAnio, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Agrega periodo
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);
        Paragraph period = new Paragraph("Período: " + start + " a " + end, dateFont);
        period.setAlignment(Element.ALIGN_CENTER);
        period.setSpacingAfter(30);
        document.add(period);

        // Crea tabla
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        // Encabezados
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        String[] titulos = {"Habitación", "Reservas", "Días Ocupados", "Ocupación %"};
        for (String titulo : titulos) {
            PdfPCell cell = new PdfPCell(new Phrase(titulo, headerFont));
            cell.setBackgroundColor(new Color(20, 41, 82));
            cell.setPadding(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Agrega filas por habitación
        for (Map.Entry<String, List<Reserva>> entry : porHabitacion.entrySet()) {
            String habitacion = entry.getKey();
            List<Reserva> res = entry.getValue();

            int numReservas = res.size();

            int diasOcupados = res.stream()
                    .mapToInt(r -> {
                        LocalDate ini = r.getFechaEntrada();
                        LocalDate fin = r.getFechaSalida();
                        LocalDate iniMes = ini.isBefore(start) ? start : ini;
                        LocalDate finMes = fin.isAfter(end) ? end : fin;
                        return (int) ChronoUnit.DAYS.between(iniMes, finMes) + 1;
                    })
                    .sum();

            double porcentaje = diasMes > 0 ? (diasOcupados * 100.0) / diasMes : 0.0;

            table.addCell(habitacion);
            table.addCell(String.valueOf(numReservas));
            table.addCell(String.valueOf(diasOcupados));
            table.addCell(String.format("%.2f%%", porcentaje));
        }

        document.add(table);
        document.close();

        byte[] pdfBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ocupacion_" + mesAnio + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // Genera PDF de ingresos mensuales
    @GetMapping("/pdf/ingresos")
    public ResponseEntity<byte[]> generarPdfIngresos(@RequestParam String mesAnio) throws Exception {
        YearMonth yearMonth = YearMonth.parse(mesAnio);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        // Filtra facturas del mes
        List<FacturaReserva> facturasMes = facturaRepo.findAll().stream()
                .filter(f -> {
                    LocalDate entrada = f.getReserva().getFechaEntrada();
                    LocalDate salida = f.getReserva().getFechaSalida();
                    return !entrada.isAfter(end) && !salida.isBefore(start);
                })
                .collect(Collectors.toList());

        // Calcula total recaudado
        double totalRecaudado = facturasMes.stream()
                .mapToDouble(FacturaReserva::getTotalFinal)
                .sum();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 70, 50);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Agrega título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(20, 41, 82));
        Paragraph title = new Paragraph("Informe de Ingresos Mensuales - " + mesAnio, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Agrega periodo
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);
        Paragraph period = new Paragraph("Período: " + start + " a " + end, dateFont);
        period.setAlignment(Element.ALIGN_CENTER);
        period.setSpacingAfter(30);
        document.add(period);

        // Crea tabla
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        // Encabezados
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        String[] headersText = {"Factura ID", "Huésped", "Hab.", "Servicios", "Habitación", "Total Final"};
        for (String h : headersText) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(20, 41, 82));
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Agrega filas de facturas
        for (FacturaReserva f : facturasMes) {
            Reserva r = f.getReserva();
            table.addCell(String.valueOf(f.getIdFactura()));
            table.addCell(r.getHuesped().getNombres() + " " + r.getHuesped().getApellidos());
            table.addCell(r.getHabitacion().getNumero());
            table.addCell("$" + String.format("%,.0f", f.getTotalServicios()));
            table.addCell("$" + String.format("%,.0f", f.getCostoHabitacion()));
            table.addCell("$" + String.format("%,.0f", f.getTotalFinal()));
        }

        document.add(table);

        // Agrega total
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(20, 41, 82));
        Paragraph total = new Paragraph("TOTAL RECAUDADO: $" + String.format("%,.2f", totalRecaudado), totalFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingBefore(30);
        document.add(total);

        document.close();

        byte[] pdfBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ingresos_" + mesAnio + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}