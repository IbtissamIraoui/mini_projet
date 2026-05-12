package com.rentcar.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.rentcar.model.Location;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─── PDF ────────────────────────────────────────────────────────────────
    public byte[] exporterLocationsPdf(List<Location> locations) throws DocumentException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        // Titre
        Font titreFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.DARK_GRAY);
        Paragraph titre = new Paragraph("Liste des Locations", titreFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        document.add(titre);

        Font dateFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);
        Paragraph dateGen = new Paragraph("Généré le : " + LocalDate.now().format(FMT), dateFont);
        dateGen.setAlignment(Element.ALIGN_CENTER);
        dateGen.setSpacingAfter(20);
        document.add(dateGen);

        // Tableau
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.5f, 2f, 2.5f, 1.8f, 1.8f, 1.5f, 1.5f});

        // En-têtes
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        String[] headers = {"ID", "Client", "Voiture", "Début", "Fin", "Montant (MAD)", "Statut"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(33, 97, 140));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        // Lignes
        Font cellFont = new Font(Font.HELVETICA, 9);
        for (Location loc : locations) {
            Color rowColor = switch (loc.getStatut()) {
                case ANNULEE -> new Color(253, 237, 236);
                case EN_COURS -> new Color(255, 249, 230);
                default -> new Color(235, 245, 235);
            };

            String[] values = {
                    "#" + loc.getId(),
                    loc.getClient().getNom(),
                    loc.getVoiture().getMarque() + " - " + loc.getVoiture().getImmatriculation(),
                    loc.getDateDebut() != null ? loc.getDateDebut().format(FMT) : "",
                    loc.getDateFin() != null ? loc.getDateFin().format(FMT) : "",
                    String.format("%.2f", loc.getMontantTotal()),
                    loc.getStatut().name()
            };

            for (String val : values) {
                PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
                cell.setBackgroundColor(rowColor);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
        }

        document.add(table);

        Font totalFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        double total = locations.stream()
                .filter(l -> l.getStatut() != Location.StatutLocation.ANNULEE)
                .mapToDouble(Location::getMontantTotal).sum();
        Paragraph totalPara = new Paragraph(
                String.format("\nTotal des locations (hors annulées) : %.2f MAD", total), totalFont);
        totalPara.setAlignment(Element.ALIGN_RIGHT);
        totalPara.setSpacingBefore(12);
        document.add(totalPara);

        document.close();
        return out.toByteArray();
    }

    // ─── EXCEL ──────────────────────────────────────────────────────────────
    public byte[] exporterLocationsExcel(List<Location> locations) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Locations");

            // Style en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // Styles lignes
            CellStyle styleAnnulee = createRowStyle(workbook, IndexedColors.ROSE);
            CellStyle styleEnCours  = createRowStyle(workbook, IndexedColors.LIGHT_YELLOW);
            CellStyle styleTerminee = createRowStyle(workbook, IndexedColors.LIGHT_GREEN);

            // En-têtes
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Client", "CIN", "Voiture", "Immatriculation", "Date début", "Date fin", "Montant (MAD)", "Statut"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Données
            int rowNum = 1;
            for (Location loc : locations) {
                Row row = sheet.createRow(rowNum++);
                CellStyle style = switch (loc.getStatut()) {
                    case ANNULEE -> styleAnnulee;
                    case EN_COURS -> styleEnCours;
                    default -> styleTerminee;
                };

                createCell(row, 0, "#" + loc.getId(), style);
                createCell(row, 1, loc.getClient().getNom(), style);
                createCell(row, 2, loc.getClient().getCin(), style);
                createCell(row, 3, loc.getVoiture().getMarque(), style);
                createCell(row, 4, loc.getVoiture().getImmatriculation(), style);
                createCell(row, 5, loc.getDateDebut() != null ? loc.getDateDebut().format(FMT) : "", style);
                createCell(row, 6, loc.getDateFin() != null ? loc.getDateFin().format(FMT) : "", style);
                createNumericCell(row, 7, loc.getMontantTotal(), style);
                createCell(row, 8, loc.getStatut().name(), style);
            }

            // Auto-taille des colonnes
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ligne total
            Row totalRow = sheet.createRow(rowNum + 1);
            CellStyle totalStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            Cell labelCell = totalRow.createCell(6);
            labelCell.setCellValue("Total (hors annulées) :");
            labelCell.setCellStyle(totalStyle);
            Cell totalCell = totalRow.createCell(7);
            double total = locations.stream()
                    .filter(l -> l.getStatut() != Location.StatutLocation.ANNULEE)
                    .mapToDouble(Location::getMontantTotal).sum();
            totalCell.setCellValue(total);
            totalCell.setCellStyle(totalStyle);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private CellStyle createRowStyle(XSSFWorkbook wb, IndexedColors color) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createNumericCell(Row row, int col, double value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}