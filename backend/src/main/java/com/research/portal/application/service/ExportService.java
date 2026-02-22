package com.research.portal.application.service;

import com.research.portal.adapter.in.web.dto.ReportDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service für den Export von Research Reports als CSV oder Excel-Datei.
 *
 * <p>Implementiert den Export nach Schweizer Standard:
 * <ul>
 *   <li>CSV: Semikolon-Trenner (CH-Standard), UTF-8 mit BOM für Excel-Kompatibilität</li>
 *   <li>Excel: XLSX-Format mit Tabellen-Formatierung, Header-Styling und Auto-Width</li>
 * </ul>
 *
 * <p>Spalten: ID, Titel, Analyst-ID, Ticker (Security-ID), Rating,
 * Kursziel, Aktueller Kurs, Upside, Risiko, Datum
 */
@Service
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final String[] HEADERS = {
            "ID", "Titel", "Analyst", "Ticker (Security-ID)",
            "Rating", "Kursziel", "Aktueller Kurs", "Upside (%)", "Risiko", "Datum"
    };

    private static final char CSV_SEPARATOR = ';';

    /**
     * Exportiert eine Liste von Reports als CSV (Semikolon-Trenner, UTF-8 mit BOM).
     *
     * @param reports die zu exportierenden Reports
     * @return Byte-Array mit CSV-Inhalt
     */
    public byte[] exportReportsCsv(List<ReportDto> reports) {
        StringBuilder sb = new StringBuilder();

        // Header-Zeile
        sb.append(buildCsvRow(HEADERS));
        sb.append("\n");

        // Datenzeilen
        for (ReportDto report : reports) {
            String[] row = {
                    safeString(report.getId()),
                    escapeCsvField(safeString(report.getTitle())),
                    safeString(report.getAnalystId()),
                    safeString(report.getSecurityId()),
                    safeString(report.getRating()),
                    safeDecimal(report.getTargetPrice()),
                    safeDecimal(report.getCurrentPrice()),
                    safeDecimal(report.getImpliedUpside()),
                    safeString(report.getRiskLevel()),
                    report.getPublishedAt() != null
                            ? report.getPublishedAt().format(DATE_FORMATTER)
                            : ""
            };
            sb.append(buildCsvRow(row));
            sb.append("\n");
        }

        // UTF-8 BOM für korrekte Excel-Darstellung
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] content = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        byte[] result = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, result, 0, bom.length);
        System.arraycopy(content, 0, result, bom.length, content.length);
        return result;
    }

    /**
     * Exportiert eine Liste von Reports als Excel-Datei (XLSX).
     *
     * <p>Das Workbook enthält:
     * <ul>
     *   <li>Formatierte Tabellen-Header (fett, blauer Hintergrund)</li>
     *   <li>Auto-Width für alle Spalten</li>
     *   <li>Zahlenformatierung für Kursziel, Aktueller Kurs und Upside</li>
     * </ul>
     *
     * @param reports die zu exportierenden Reports
     * @return Byte-Array mit XLSX-Inhalt
     * @throws IOException wenn die Workbook-Erstellung fehlschlägt
     */
    public byte[] exportReportsExcel(List<ReportDto> reports) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Research Reports");

            // Header-Stil erstellen
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Zahlenformat für Finanzwerte
            DataFormat dataFormat = workbook.createDataFormat();
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));

            // Header-Zeile
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datenzeilen
            int rowIndex = 1;
            for (ReportDto report : reports) {
                Row row = sheet.createRow(rowIndex++);

                createCell(row, 0, report.getId() != null ? report.getId().toString() : "");
                createCell(row, 1, safeString(report.getTitle()));
                createCell(row, 2, report.getAnalystId() != null ? report.getAnalystId().toString() : "");
                createCell(row, 3, report.getSecurityId() != null ? report.getSecurityId().toString() : "");
                createCell(row, 4, safeString(report.getRating()));

                // Numerische Spalten mit Formatierung
                createNumericCell(row, 5, report.getTargetPrice(), numberStyle);
                createNumericCell(row, 6, report.getCurrentPrice(), numberStyle);
                createNumericCell(row, 7, report.getImpliedUpside(), numberStyle);

                createCell(row, 8, safeString(report.getRiskLevel()));
                createCell(row, 9, report.getPublishedAt() != null
                        ? report.getPublishedAt().format(DATE_FORMATTER) : "");
            }

            // Auto-Width für alle Spalten (nach Befüllen der Daten)
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                // Mindestbreite und etwas Puffer hinzufügen
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.max(currentWidth + 512, 3000));
            }

            // Auto-Filter setzen
            if (!reports.isEmpty()) {
                sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, HEADERS.length - 1));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // Erstellt den Header-Stil: fett, weisser Text, blauer Hintergrund
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Blauer Hintergrund (ZKB-Blauton)
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Fetter, weisser Text
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        // Rahmen
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.DARK_BLUE.getIndex());

        return style;
    }

    private void createCell(Row row, int col, String value) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
    }

    private void createNumericCell(Row row, int col, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(col);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
            cell.setCellStyle(style);
        } else {
            cell.setCellValue("");
        }
    }

    private String buildCsvRow(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(CSV_SEPARATOR);
            }
            sb.append(fields[i] != null ? fields[i] : "");
        }
        return sb.toString();
    }

    private String escapeCsvField(String value) {
        if (value == null) return "";
        // Felder mit Semikolon, Zeilenumbruch oder Anführungszeichen in Anführungszeichen einschliessen
        if (value.contains(";") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String safeString(Object value) {
        return value != null ? value.toString() : "";
    }

    private String safeDecimal(BigDecimal value) {
        return value != null ? value.toPlainString() : "";
    }
}
