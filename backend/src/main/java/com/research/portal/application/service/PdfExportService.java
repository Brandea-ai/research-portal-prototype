package com.research.portal.application.service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.research.portal.adapter.in.web.dto.ReportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service für den PDF-Export einzelner Research Reports.
 *
 * <p>Erzeugt strukturierte PDF-Dokumente mit Header, Meta-Daten-Tabelle,
 * Executive Summary sowie Katalysatoren und Risiken als Bullet-Listen.
 * Verwendet OpenPDF (MIT-Lizenz) mit Standard-Helvetica-Schriftarten.
 */
@Service
public class PdfExportService {

    private static final Logger log = LoggerFactory.getLogger(PdfExportService.class);

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Farben (RGB)
    private static final Color COLOR_BUY        = new Color(0x22, 0x8B, 0x22); // Forest Green
    private static final Color COLOR_STRONG_BUY = new Color(0x00, 0x64, 0x00); // Dark Green
    private static final Color COLOR_SELL       = new Color(0xCC, 0x00, 0x00); // Dark Red
    private static final Color COLOR_STRONG_SELL= new Color(0x8B, 0x00, 0x00); // Maroon
    private static final Color COLOR_HOLD       = new Color(0x70, 0x80, 0x90); // Slate Gray
    private static final Color COLOR_HEADER_BG  = new Color(0x1A, 0x1D, 0x2A); // Dark Navy
    private static final Color COLOR_TABLE_HEADER = new Color(0x2A, 0x30, 0x48);
    private static final Color COLOR_WHITE      = Color.WHITE;
    private static final Color COLOR_TEXT_DARK  = new Color(0x1A, 0x1D, 0x24);
    private static final Color COLOR_BORDER     = new Color(0xD8, 0xDC, 0xE4);

    // Schriftarten
    private final Font fontHeaderTitle;
    private final Font fontHeaderSub;
    private final Font fontReportTitle;
    private final Font fontSectionHeader;
    private final Font fontBodyBold;
    private final Font fontBody;
    private final Font fontSmall;
    private final Font fontBullet;
    private final Font fontRatingWhite;

    public PdfExportService() {
        fontHeaderTitle  = new Font(Font.HELVETICA, 10f, Font.BOLD,   COLOR_WHITE);
        fontHeaderSub    = new Font(Font.HELVETICA,  8f, Font.NORMAL, new Color(0xA0, 0xAA, 0xB8));
        fontReportTitle  = new Font(Font.HELVETICA, 18f, Font.BOLD,   COLOR_TEXT_DARK);
        fontSectionHeader= new Font(Font.HELVETICA, 11f, Font.BOLD,   COLOR_TEXT_DARK);
        fontBodyBold     = new Font(Font.HELVETICA,  9f, Font.BOLD,   COLOR_TEXT_DARK);
        fontBody         = new Font(Font.HELVETICA,  9f, Font.NORMAL, COLOR_TEXT_DARK);
        fontSmall        = new Font(Font.HELVETICA,  7f, Font.NORMAL, new Color(0x6C, 0x7A, 0x8D));
        fontBullet       = new Font(Font.HELVETICA,  9f, Font.NORMAL, COLOR_TEXT_DARK);
        fontRatingWhite  = new Font(Font.HELVETICA, 10f, Font.BOLD,   COLOR_WHITE);
    }

    /**
     * Exportiert einen einzelnen Research Report als PDF-Byte-Array.
     *
     * @param report das zu exportierende ReportDto
     * @return PDF-Dokument als byte[]
     */
    public byte[] exportReportPdf(ReportDto report) {
        log.info("PDF-Export gestartet für Report ID: {}", report != null ? report.getId() : "null");

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 40f, 40f, 60f, 60f);
        PdfWriter writer = PdfWriter.getInstance(document, out);

        // Footer-Event registrieren
        writer.setPageEvent(new FooterEvent());

        document.open();

        addHeaderBanner(document, report);
        addReportTitle(document, report);
        addMetaDataTable(document, report);
        addExecutiveSummary(document, report);
        addCatalysts(document, report);
        addRisks(document, report);

        document.close();

        log.info("PDF-Export abgeschlossen, Groesse: {} Bytes", out.size());
        return out.toByteArray();
    }

    // Header-Banner (dunkler Hintergrund, Titel + Datum)
    private void addHeaderBanner(Document document, ReportDto report) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100f);
        headerTable.setWidths(new float[]{3f, 1f});

        // Linke Seite: "RESEARCH REPORT"
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBackgroundColor(COLOR_HEADER_BG);
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(12f);

        Paragraph headerLabel = new Paragraph("RESEARCH REPORT", fontHeaderTitle);
        leftCell.addElement(headerLabel);

        String typeText = (report != null && report.getReportType() != null)
                ? report.getReportType()
                : "REPORT";
        Paragraph typeLabel = new Paragraph("Type: " + typeText, fontHeaderSub);
        leftCell.addElement(typeLabel);

        // Rechte Seite: Datum
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBackgroundColor(COLOR_HEADER_BG);
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(12f);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        String dateStr = (report != null && report.getPublishedAt() != null)
                ? report.getPublishedAt().format(DATE_FORMAT)
                : LocalDate.now().format(DATE_FORMAT);
        Paragraph dateLabel = new Paragraph(dateStr, fontHeaderTitle);
        dateLabel.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(dateLabel);

        Paragraph confLabel = new Paragraph("Vertraulich", fontHeaderSub);
        confLabel.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(confLabel);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);
        headerTable.setSpacingAfter(16f);
        document.add(headerTable);
    }

    // Titel des Reports (gross, fett)
    private void addReportTitle(Document document, ReportDto report) throws DocumentException {
        String title = (report != null && report.getTitle() != null)
                ? report.getTitle()
                : "Research Report";

        Paragraph titlePara = new Paragraph(title, fontReportTitle);
        titlePara.setSpacingAfter(16f);
        document.add(titlePara);
    }

    // Meta-Daten-Tabelle: Analyst-ID, Ticker (Security-ID), Rating, Kursziel etc.
    private void addMetaDataTable(Document document, ReportDto report) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.5f, 2f, 1.5f, 2f});
        table.setSpacingAfter(16f);

        // Rating-Farbe ermitteln
        Color ratingColor = getRatingColor(report != null ? report.getRating() : null);

        // Analyst
        addMetaCell(table, "Analyst", formatAnalystId(report), false, null);

        // Ticker / Security
        addMetaCell(table, "Wertschrift (ID)", formatSecurityId(report), false, null);

        // Rating (farblich hervorgehoben)
        String ratingValue = (report != null && report.getRating() != null)
                ? report.getRating()
                : "N/A";
        addMetaCell(table, "Rating", ratingValue, true, ratingColor);

        // Risiko
        String riskValue = (report != null && report.getRiskLevel() != null)
                ? report.getRiskLevel()
                : "N/A";
        addMetaCell(table, "Risiko", riskValue, false, null);

        // Kursziel
        String targetValue = (report != null && report.getTargetPrice() != null)
                ? "CHF " + report.getTargetPrice().toPlainString()
                : "N/A";
        addMetaCell(table, "Kursziel", targetValue, false, null);

        // Aktueller Kurs
        String currentValue = (report != null && report.getCurrentPrice() != null)
                ? "CHF " + report.getCurrentPrice().toPlainString()
                : "N/A";
        addMetaCell(table, "Aktueller Kurs", currentValue, false, null);

        // Upside
        String upsideValue = (report != null && report.getImpliedUpside() != null)
                ? report.getImpliedUpside().toPlainString() + "%"
                : "N/A";
        addMetaCell(table, "Upside", upsideValue, false, null);

        // Report-ID
        String reportId = (report != null && report.getId() != null)
                ? "#" + report.getId()
                : "N/A";
        addMetaCell(table, "Report-ID", reportId, false, null);

        document.add(table);
    }

    private void addMetaCell(PdfPTable table, String label, String value,
                             boolean highlight, Color highlightColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(COLOR_BORDER);
        cell.setBorderWidth(0.5f);
        cell.setPadding(8f);

        if (highlight && highlightColor != null) {
            cell.setBackgroundColor(highlightColor);
            Paragraph labelPara = new Paragraph(label, new Font(Font.HELVETICA, 7f, Font.NORMAL, COLOR_WHITE));
            Paragraph valuePara = new Paragraph(value, fontRatingWhite);
            cell.addElement(labelPara);
            cell.addElement(valuePara);
        } else {
            Paragraph labelPara = new Paragraph(label, fontSmall);
            Paragraph valuePara = new Paragraph(value, fontBodyBold);
            cell.addElement(labelPara);
            cell.addElement(valuePara);
        }

        table.addCell(cell);
    }

    // Executive Summary
    private void addExecutiveSummary(Document document, ReportDto report) throws DocumentException {
        addSectionHeader(document, "Executive Summary");

        String summary = (report != null && report.getExecutiveSummary() != null)
                ? report.getExecutiveSummary()
                : "";

        Paragraph para = new Paragraph(summary, fontBody);
        para.setLeading(14f);
        para.setSpacingAfter(16f);
        document.add(para);
    }

    // Katalysatoren als Bullet-Liste
    private void addCatalysts(Document document, ReportDto report) throws DocumentException {
        List<String> catalysts = (report != null) ? report.getInvestmentCatalysts() : null;
        if (catalysts == null || catalysts.isEmpty()) {
            return;
        }

        addSectionHeader(document, "Investment Catalysts");

        for (String catalyst : catalysts) {
            if (catalyst == null) continue;
            Chunk bullet = new Chunk("\u2022  ", fontBullet);
            Chunk text = new Chunk(catalyst, fontBullet);
            Paragraph item = new Paragraph();
            item.add(bullet);
            item.add(text);
            item.setIndentationLeft(16f);
            item.setSpacingAfter(4f);
            document.add(item);
        }

        document.add(new Paragraph(" "));
    }

    // Risiken als Bullet-Liste
    private void addRisks(Document document, ReportDto report) throws DocumentException {
        List<String> risks = (report != null) ? report.getKeyRisks() : null;
        if (risks == null || risks.isEmpty()) {
            return;
        }

        addSectionHeader(document, "Key Risks");

        for (String risk : risks) {
            if (risk == null) continue;
            Chunk bullet = new Chunk("\u2022  ", fontBullet);
            Chunk text = new Chunk(risk, fontBullet);
            Paragraph item = new Paragraph();
            item.add(bullet);
            item.add(text);
            item.setIndentationLeft(16f);
            item.setSpacingAfter(4f);
            document.add(item);
        }
    }

    private void addSectionHeader(Document document, String title) throws DocumentException {
        // Horizontale Linie simuliert durch eine schmale Tabelle
        Paragraph header = new Paragraph(title, fontSectionHeader);
        header.setSpacingBefore(8f);
        header.setSpacingAfter(6f);
        document.add(header);

        // Trennlinie
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100f);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.BOTTOM);
        lineCell.setBorderColor(COLOR_BORDER);
        lineCell.setBorderWidth(0.8f);
        lineCell.setPadding(0f);
        lineCell.setFixedHeight(1f);
        line.addCell(lineCell);
        line.setSpacingAfter(8f);
        document.add(line);
    }

    // Rating-Farbe je nach Rating-Wert
    private Color getRatingColor(String rating) {
        if (rating == null) return COLOR_HOLD;
        return switch (rating.toUpperCase()) {
            case "BUY"         -> COLOR_BUY;
            case "STRONG_BUY"  -> COLOR_STRONG_BUY;
            case "SELL"        -> COLOR_SELL;
            case "STRONG_SELL" -> COLOR_STRONG_SELL;
            default            -> COLOR_HOLD; // HOLD und andere
        };
    }

    private String formatAnalystId(ReportDto report) {
        if (report == null || report.getAnalystId() == null) return "N/A";
        return "Analyst #" + report.getAnalystId();
    }

    private String formatSecurityId(ReportDto report) {
        if (report == null || report.getSecurityId() == null) return "N/A";
        return "Security #" + report.getSecurityId();
    }

    /**
     * PdfPageEvent-Implementierung für den Footer auf jeder Seite.
     * Schreibt "Confidential — For Internal Use Only" und die Seitennummer.
     */
    static class FooterEvent extends PdfPageEventHelper {

        private static final Font FOOTER_FONT =
                new Font(Font.HELVETICA, 7f, Font.NORMAL, new Color(0x6C, 0x7A, 0x8D));

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Rectangle pageSize = document.getPageSize();

            float footerY = document.bottomMargin() - 15f;
            float leftX  = document.leftMargin();
            float rightX = pageSize.getRight() - document.rightMargin();

            // Linker Text: Confidential
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_LEFT,
                    new Phrase("Confidential \u2014 For Internal Use Only", FOOTER_FONT),
                    leftX,
                    footerY,
                    0
            );

            // Rechter Text: Seitennummer
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_RIGHT,
                    new Phrase("Seite " + writer.getPageNumber(), FOOTER_FONT),
                    rightX,
                    footerY,
                    0
            );
        }
    }
}
