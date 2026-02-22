package com.research.portal.application.service;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import com.research.portal.adapter.in.web.dto.ReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit Tests fuer PdfExportService.
 *
 * <p>Prueft, dass das erzeugte PDF-Dokument korrekt aufgebaut ist
 * und die wesentlichen Inhalte enthaelt. Kein Spring-Context noetig,
 * da PdfExportService keine Spring-Abhaengigkeiten besitzt.
 *
 * <p>Fuer Text-Extraktion wird der PdfTextExtractor aus OpenPDF verwendet,
 * der den FlateDecode-komprimierten Inhalt korrekt liest.
 */
@DisplayName("PdfExportService Tests")
class PdfExportServiceTest {

    private PdfExportService pdfExportService;

    @BeforeEach
    void setUp() {
        pdfExportService = new PdfExportService();
    }

    // Hilfsmethode: erzeugt einen vollstaendig befuellten Test-ReportDto
    private ReportDto createFullTestReport() {
        ReportDto dto = new ReportDto();
        dto.setId(42L);
        dto.setTitle("Nestle SA - Initiation of Coverage: Defensiver Qualitaetswert");
        dto.setAnalystId(7L);
        dto.setSecurityId(3L);
        dto.setRating("BUY");
        dto.setPreviousRating("HOLD");
        dto.setRatingChanged(true);
        dto.setTargetPrice(new BigDecimal("105.00"));
        dto.setCurrentPrice(new BigDecimal("89.42"));
        dto.setImpliedUpside(new BigDecimal("17.43"));
        dto.setRiskLevel("MEDIUM");
        dto.setReportType("INITIATION");
        dto.setPublishedAt(LocalDateTime.of(2026, 2, 20, 9, 30));
        dto.setExecutiveSummary(
                "Wir initiieren die Coverage von Nestle mit einem BUY-Rating und einem "
                        + "Kursziel von CHF 105. Das Unternehmen bietet defensives Wachstum "
                        + "mit starker Dividendenrendite."
        );
        dto.setInvestmentCatalysts(List.of(
                "Margenexpansion durch Pricing Power",
                "Aktienrueckkaufprogramm CHF 5 Mrd.",
                "Wachstum in Emerging Markets"
        ));
        dto.setKeyRisks(List.of(
                "Waehrungsrisiko durch globale Diversifikation",
                "Input-Kosten-Inflation",
                "Regulatorische Risiken in China"
        ));
        dto.setTags(List.of("Schweiz", "Consumer Staples", "Dividende"));
        return dto;
    }

    @Test
    @DisplayName("PDF-Export erzeugt nicht-leeres Byte-Array das mit %PDF beginnt")
    void testPdfExport_createsPdf() {
        ReportDto report = createFullTestReport();

        byte[] result = pdfExportService.exportReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();

        // PDF-Magic-Bytes: %PDF
        assertThat(result[0]).isEqualTo((byte) '%');
        assertThat(result[1]).isEqualTo((byte) 'P');
        assertThat(result[2]).isEqualTo((byte) 'D');
        assertThat(result[3]).isEqualTo((byte) 'F');
    }

    @Test
    @DisplayName("PDF-Export enthaelt den Titel des Reports")
    void testPdfExport_containsTitle() throws IOException {
        ReportDto report = createFullTestReport();
        report.setTitle("Nestle SA - Initiation of Coverage");

        byte[] result = pdfExportService.exportReportPdf(report);
        String text = extractAllText(result);

        assertThat(text).contains("Nestle SA");
    }

    @Test
    @DisplayName("PDF-Export enthaelt das Rating des Reports")
    void testPdfExport_containsRating() throws IOException {
        ReportDto report = createFullTestReport();
        report.setRating("BUY");

        byte[] result = pdfExportService.exportReportPdf(report);
        String text = extractAllText(result);

        assertThat(text).contains("BUY");
    }

    @Test
    @DisplayName("PDF-Export enthaelt die Analyst-ID")
    void testPdfExport_containsAnalystName() throws IOException {
        ReportDto report = createFullTestReport();
        report.setAnalystId(7L);

        byte[] result = pdfExportService.exportReportPdf(report);
        String text = extractAllText(result);

        // Analyst-ID wird als "Analyst #7" dargestellt
        assertThat(text).contains("7");
    }

    @Test
    @DisplayName("PDF-Export enthaelt die Executive Summary")
    void testPdfExport_containsExecutiveSummary() throws IOException {
        ReportDto report = createFullTestReport();
        report.setExecutiveSummary("Spezielle Zusammenfassung fuer den Testfall Nummer 99.");

        byte[] result = pdfExportService.exportReportPdf(report);
        String text = extractAllText(result);

        assertThat(text).contains("Zusammenfassung");
    }

    @Test
    @DisplayName("PDF-Export behandelt null-Felder ohne Exception (graceful handling)")
    void testPdfExport_withNullFields() {
        ReportDto report = new ReportDto();
        // Alle Felder bleiben null

        byte[] result = pdfExportService.exportReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();

        // Muss trotzdem ein gueltiges PDF sein
        assertThat(result[0]).isEqualTo((byte) '%');
        assertThat(result[1]).isEqualTo((byte) 'P');
        assertThat(result[2]).isEqualTo((byte) 'D');
        assertThat(result[3]).isEqualTo((byte) 'F');
    }

    @Test
    @DisplayName("PDF-Export mit SELL-Rating erzeugt valides PDF")
    void testPdfExport_sellRating() {
        ReportDto report = createFullTestReport();
        report.setRating("SELL");

        byte[] result = pdfExportService.exportReportPdf(report);

        assertThat(result).isNotEmpty();
        assertThat(result[0]).isEqualTo((byte) '%');
        assertThat(result[1]).isEqualTo((byte) 'P');
        assertThat(result[2]).isEqualTo((byte) 'D');
        assertThat(result[3]).isEqualTo((byte) 'F');
    }

    @Test
    @DisplayName("PDF-Export mit HOLD-Rating erzeugt valides PDF")
    void testPdfExport_holdRating() {
        ReportDto report = createFullTestReport();
        report.setRating("HOLD");

        byte[] result = pdfExportService.exportReportPdf(report);

        assertThat(result).isNotEmpty();
        assertThat(result[0]).isEqualTo((byte) '%');
    }

    @Test
    @DisplayName("PDF-Export mit leerer Katalysator-Liste wirft keine Exception")
    void testPdfExport_emptyCatalysts() {
        ReportDto report = createFullTestReport();
        report.setInvestmentCatalysts(List.of());
        report.setKeyRisks(List.of());

        byte[] result = pdfExportService.exportReportPdf(report);

        assertThat(result).isNotEmpty();
        assertThat(result[0]).isEqualTo((byte) '%');
    }

    @Test
    @DisplayName("PDF-Export mit null-Katalysatoren wirft keine Exception")
    void testPdfExport_nullCatalystsAndRisks() {
        ReportDto report = createFullTestReport();
        report.setInvestmentCatalysts(null);
        report.setKeyRisks(null);

        byte[] result = pdfExportService.exportReportPdf(report);

        assertThat(result).isNotEmpty();
    }

    /**
     * Extrahiert den gesamten Text aus allen Seiten des PDFs
     * mittels OpenPDF PdfTextExtractor (unterstuetzt FlateDecode-Kompression).
     */
    private String extractAllText(byte[] pdfBytes) throws IOException {
        PdfReader reader = new PdfReader(pdfBytes);
        PdfTextExtractor extractor = new PdfTextExtractor(reader);
        StringBuilder sb = new StringBuilder();
        int pages = reader.getNumberOfPages();
        for (int i = 1; i <= pages; i++) {
            sb.append(extractor.getTextFromPage(i));
            sb.append(' ');
        }
        reader.close();
        return sb.toString();
    }
}
