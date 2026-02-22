package com.research.portal.application.service;

import com.research.portal.adapter.in.web.dto.ReportDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit Tests für ExportService.
 *
 * Testet CSV- und Excel-Export mit Testdaten.
 * Kein Spring Context nötig, da ExportService keine Spring-Abhängigkeiten hat.
 */
@DisplayName("ExportService Tests")
class ExportServiceTest {

    private ExportService exportService;

    @BeforeEach
    void setUp() {
        exportService = new ExportService();
    }

    // Hilfsmethode: erstellt einen vollständig befüllten Test-ReportDto
    private ReportDto createTestReport(Long id, String title, String rating) {
        ReportDto dto = new ReportDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setAnalystId(1L);
        dto.setSecurityId(2L);
        dto.setRating(rating);
        dto.setTargetPrice(new BigDecimal("105.50"));
        dto.setCurrentPrice(new BigDecimal("89.42"));
        dto.setImpliedUpside(new BigDecimal("17.98"));
        dto.setRiskLevel("MEDIUM");
        dto.setPublishedAt(LocalDateTime.of(2026, 2, 20, 9, 30));
        return dto;
    }

    @Nested
    @DisplayName("CSV Export")
    class CsvExportTests {

        @Test
        @DisplayName("CSV enthält alle Header-Spalten")
        void testCsvExport_containsHeaders() {
            List<ReportDto> reports = List.of(createTestReport(1L, "Nestlé Analyse", "BUY"));

            byte[] result = exportService.exportReportsCsv(reports);
            String csv = stripBom(result);
            String headerLine = csv.split("\n")[0];

            assertThat(headerLine).contains("ID");
            assertThat(headerLine).contains("Titel");
            assertThat(headerLine).contains("Analyst");
            assertThat(headerLine).contains("Rating");
            assertThat(headerLine).contains("Kursziel");
            assertThat(headerLine).contains("Aktueller Kurs");
            assertThat(headerLine).contains("Upside");
            assertThat(headerLine).contains("Risiko");
            assertThat(headerLine).contains("Datum");
        }

        @Test
        @DisplayName("CSV enthält die Report-Daten")
        void testCsvExport_containsData() {
            List<ReportDto> reports = List.of(
                    createTestReport(1L, "Nestlé Analyse", "BUY"),
                    createTestReport(2L, "Novartis Update", "HOLD")
            );

            byte[] result = exportService.exportReportsCsv(reports);
            String csv = stripBom(result);

            assertThat(csv).contains("Nestlé Analyse");
            assertThat(csv).contains("BUY");
            assertThat(csv).contains("Novartis Update");
            assertThat(csv).contains("HOLD");
            assertThat(csv).contains("105.5");
            assertThat(csv).contains("89.42");
            assertThat(csv).contains("17.98");
            assertThat(csv).contains("MEDIUM");
            assertThat(csv).contains("20.02.2026");
        }

        @Test
        @DisplayName("CSV verwendet Semikolon als Trenner")
        void testCsvExport_semicolonSeparator() {
            List<ReportDto> reports = List.of(createTestReport(1L, "Test Report", "BUY"));

            byte[] result = exportService.exportReportsCsv(reports);
            String csv = stripBom(result);
            String headerLine = csv.split("\n")[0];

            // Semikolon muss als Trenner vorhanden sein
            assertThat(headerLine).contains(";");

            // Kein Komma als Trenner zwischen den Feldern (Schweizer Standard)
            long semicolonCount = headerLine.chars().filter(c -> c == ';').count();
            assertThat(semicolonCount).isGreaterThanOrEqualTo(9); // 10 Spalten = 9 Trenner
        }

        @Test
        @DisplayName("CSV mit leerer Liste liefert nur Header-Zeile")
        void testCsvExport_emptyList() {
            byte[] result = exportService.exportReportsCsv(List.of());
            String csv = stripBom(result);
            String[] lines = csv.split("\n");

            // Nur Header-Zeile, keine Datenzeilen
            assertThat(lines).hasSize(1);
            assertThat(lines[0]).contains("ID");
            assertThat(lines[0]).contains("Titel");
        }

        @Test
        @DisplayName("CSV enthält UTF-8 BOM am Anfang")
        void testCsvExport_containsBom() {
            byte[] result = exportService.exportReportsCsv(List.of());

            // UTF-8 BOM: 0xEF, 0xBB, 0xBF
            assertThat(result).hasSizeGreaterThanOrEqualTo(3);
            assertThat(result[0]).isEqualTo((byte) 0xEF);
            assertThat(result[1]).isEqualTo((byte) 0xBB);
            assertThat(result[2]).isEqualTo((byte) 0xBF);
        }

        @Test
        @DisplayName("CSV escaped Felder mit Semikolon korrekt")
        void testCsvExport_escapesFieldsWithSemicolon() {
            ReportDto report = createTestReport(1L, "Titel; mit Semikolon", "BUY");
            byte[] result = exportService.exportReportsCsv(List.of(report));
            String csv = stripBom(result);

            // Das Feld mit Semikolon muss in Anführungszeichen stehen
            assertThat(csv).contains("\"Titel; mit Semikolon\"");
        }
    }

    @Nested
    @DisplayName("Excel Export")
    class ExcelExportTests {

        @Test
        @DisplayName("Excel erstellt valides Workbook")
        void testExcelExport_createsWorkbook() throws IOException {
            List<ReportDto> reports = List.of(createTestReport(1L, "Nestlé Analyse", "BUY"));

            byte[] result = exportService.exportReportsExcel(reports);

            assertThat(result).isNotEmpty();

            // Workbook muss parsebar sein
            try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
                assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
                assertThat(workbook.getSheetAt(0)).isNotNull();
            }
        }

        @Test
        @DisplayName("Excel enthält alle Header in der ersten Zeile")
        void testExcelExport_hasHeaders() throws IOException {
            List<ReportDto> reports = List.of(createTestReport(1L, "Nestlé Analyse", "BUY"));

            byte[] result = exportService.exportReportsExcel(reports);

            try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);

                assertThat(headerRow).isNotNull();

                // Header-Werte überprüfen
                List<String> headers = List.of(
                        "ID", "Titel", "Analyst", "Ticker (Security-ID)",
                        "Rating", "Kursziel", "Aktueller Kurs", "Upside (%)", "Risiko", "Datum"
                );

                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = headerRow.getCell(i);
                    assertThat(cell).isNotNull();
                    assertThat(cell.getStringCellValue()).isEqualTo(headers.get(i));
                }
            }
        }

        @Test
        @DisplayName("Excel enthält die Report-Daten in Datenzeilen")
        void testExcelExport_hasData() throws IOException {
            List<ReportDto> reports = List.of(
                    createTestReport(1L, "Nestlé Analyse", "BUY"),
                    createTestReport(2L, "Novartis Update", "HOLD")
            );

            byte[] result = exportService.exportReportsExcel(reports);

            try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
                Sheet sheet = workbook.getSheetAt(0);

                // Zeile 0: Header, Zeile 1: erster Report, Zeile 2: zweiter Report
                assertThat(sheet.getLastRowNum()).isEqualTo(2);

                // Erste Datenzeile (Index 1)
                Row row1 = sheet.getRow(1);
                assertThat(row1).isNotNull();
                assertThat(row1.getCell(0).getStringCellValue()).isEqualTo("1");
                assertThat(row1.getCell(1).getStringCellValue()).isEqualTo("Nestlé Analyse");
                assertThat(row1.getCell(4).getStringCellValue()).isEqualTo("BUY");

                // Zweite Datenzeile (Index 2)
                Row row2 = sheet.getRow(2);
                assertThat(row2).isNotNull();
                assertThat(row2.getCell(0).getStringCellValue()).isEqualTo("2");
                assertThat(row2.getCell(1).getStringCellValue()).isEqualTo("Novartis Update");
                assertThat(row2.getCell(4).getStringCellValue()).isEqualTo("HOLD");
            }
        }

        @Test
        @DisplayName("Excel-Header hat fetten Schriftstil")
        void testExcelExport_headerIsBold() throws IOException {
            List<ReportDto> reports = List.of(createTestReport(1L, "Test", "BUY"));

            byte[] result = exportService.exportReportsExcel(reports);

            try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);
                Cell firstCell = headerRow.getCell(0);

                Font font = workbook.getFontAt(firstCell.getCellStyle().getFontIndex());
                assertThat(font.getBold()).isTrue();
            }
        }

        @Test
        @DisplayName("Excel mit leerer Liste enthält nur Header-Zeile")
        void testExcelExport_emptyList() throws IOException {
            byte[] result = exportService.exportReportsExcel(List.of());

            try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
                Sheet sheet = workbook.getSheetAt(0);

                // Nur Header-Zeile (Index 0), keine Datenzeilen
                assertThat(sheet.getLastRowNum()).isEqualTo(0);

                // Header muss trotzdem vorhanden sein
                Row headerRow = sheet.getRow(0);
                assertThat(headerRow).isNotNull();
                assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("ID");
            }
        }

        @Test
        @DisplayName("Excel hat korrekte Sheetbezeichnung")
        void testExcelExport_sheetName() throws IOException {
            byte[] result = exportService.exportReportsExcel(List.of());

            try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
                assertThat(workbook.getSheetName(0)).isEqualTo("Research Reports");
            }
        }

        @Test
        @DisplayName("Excel numerische Felder haben Numerikstyp")
        void testExcelExport_numericFieldsHaveNumericType() throws IOException {
            List<ReportDto> reports = List.of(createTestReport(1L, "Nestlé", "BUY"));

            byte[] result = exportService.exportReportsExcel(reports);

            try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
                Sheet sheet = workbook.getSheetAt(0);
                Row dataRow = sheet.getRow(1);

                // Kursziel (Spalte 5), Aktueller Kurs (6), Upside (7) müssen numerisch sein
                assertThat(dataRow.getCell(5).getCellType()).isEqualTo(CellType.NUMERIC);
                assertThat(dataRow.getCell(6).getCellType()).isEqualTo(CellType.NUMERIC);
                assertThat(dataRow.getCell(7).getCellType()).isEqualTo(CellType.NUMERIC);

                assertThat(dataRow.getCell(5).getNumericCellValue()).isEqualTo(105.50);
                assertThat(dataRow.getCell(6).getNumericCellValue()).isEqualTo(89.42);
            }
        }
    }

    // BOM vom CSV-Inhalt entfernen für String-Vergleiche
    private String stripBom(byte[] bytes) {
        if (bytes.length >= 3
                && bytes[0] == (byte) 0xEF
                && bytes[1] == (byte) 0xBB
                && bytes[2] == (byte) 0xBF) {
            return new String(bytes, 3, bytes.length - 3, StandardCharsets.UTF_8);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
