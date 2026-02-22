package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.ErrorResponse;
import com.research.portal.adapter.in.web.dto.ReportDto;
import com.research.portal.adapter.in.web.mapper.ReportApiMapper;
import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.application.service.PdfExportService;
import com.research.portal.domain.port.in.GetReportsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * REST-Controller für den PDF-Export einzelner Research Reports.
 *
 * <p>Endpunkte:
 * <ul>
 *   <li>{@code GET /api/export/reports/{id}/pdf} — Einzelner Report als PDF</li>
 * </ul>
 *
 * <p>Liefert die PDF-Datei als Attachment (Content-Disposition: attachment).
 * Bei unbekannter Report-ID wird HTTP 404 zurückgegeben.
 */
@RestController
@RequestMapping("/api/export")
@Tag(name = "Export", description = "Datenexport für Research Reports (CSV, Excel und PDF)")
public class PdfExportController {

    private static final Logger log = LoggerFactory.getLogger(PdfExportController.class);
    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private final GetReportsUseCase getReports;
    private final ReportApiMapper reportMapper;
    private final PdfExportService pdfExportService;

    public PdfExportController(GetReportsUseCase getReports,
                               ReportApiMapper reportMapper,
                               PdfExportService pdfExportService) {
        this.getReports = getReports;
        this.reportMapper = reportMapper;
        this.pdfExportService = pdfExportService;
    }

    /**
     * Exportiert einen einzelnen Research Report als PDF-Datei.
     *
     * <p>Das PDF enthält Header mit Datum und Report-Typ, eine Meta-Daten-Tabelle,
     * die Executive Summary sowie Katalysatoren und Risiken als Bullet-Listen.
     * Das Rating wird farblich hervorgehoben (BUY=Grün, SELL=Rot, HOLD=Grau).
     *
     * @param id die eindeutige ID des Reports
     * @return PDF-Datei als Download (application/pdf)
     * @throws ResourceNotFoundException wenn kein Report mit dieser ID existiert
     */
    @GetMapping("/reports/{id}/pdf")
    @Operation(
            summary = "Research Report als PDF exportieren",
            description = "Exportiert einen einzelnen Research Report als PDF-Datei. "
                    + "Das Dokument enthält Header, Meta-Daten-Tabelle mit farbig hervorgehobenem Rating, "
                    + "Executive Summary sowie Katalysatoren und Risiken als Bullet-Listen. "
                    + "Farbcodierung: BUY=Grün, SELL=Rot, HOLD=Grau."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "PDF-Datei erfolgreich erstellt",
                    content = @Content(mediaType = "application/pdf")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report nicht gefunden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<byte[]> exportReportPdf(
            @Parameter(description = "Eindeutige Report-ID", example = "1")
            @PathVariable Long id) {

        ReportDto report = getReports.getReportById(id)
                .map(reportMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Report", id));

        byte[] pdfBytes = pdfExportService.exportReportPdf(report);

        String filename = buildFilename(id);
        log.info("PDF-Export abgeschlossen fuer Report ID {}: {} Bytes", id, pdfBytes.length);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .body(pdfBytes);
    }

    private String buildFilename(Long reportId) {
        String date = LocalDate.now().format(FILE_DATE_FORMAT);
        return "research-report-" + reportId + "-" + date + ".pdf";
    }
}
