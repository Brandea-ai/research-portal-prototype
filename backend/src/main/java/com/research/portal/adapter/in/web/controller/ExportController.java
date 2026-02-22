package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.ReportDto;
import com.research.portal.adapter.in.web.mapper.ReportApiMapper;
import com.research.portal.application.service.ExportService;
import com.research.portal.domain.port.in.GetReportsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST-Controller für den Datenexport von Research Reports.
 *
 * <p>Bietet Endpunkte für den Export aller publizierten Research Reports
 * in den Formaten CSV und Excel (XLSX).
 *
 * <p>Endpunkte:
 * <ul>
 *   <li>{@code GET /api/export/reports/csv}   — Export als CSV (Semikolon-Trenner)</li>
 *   <li>{@code GET /api/export/reports/excel} — Export als XLSX</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/export")
@Tag(name = "Export", description = "Datenexport für Research Reports (CSV und Excel)")
public class ExportController {

    private static final Logger log = LoggerFactory.getLogger(ExportController.class);
    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private final GetReportsUseCase getReports;
    private final ReportApiMapper reportMapper;
    private final ExportService exportService;

    public ExportController(GetReportsUseCase getReports,
                            ReportApiMapper reportMapper,
                            ExportService exportService) {
        this.getReports = getReports;
        this.reportMapper = reportMapper;
        this.exportService = exportService;
    }

    /**
     * Exportiert alle Research Reports als CSV-Datei.
     *
     * <p>Das CSV verwendet Semikolon als Trenner (Schweizer Standard) und
     * UTF-8-Encoding mit BOM für korrekte Darstellung in Microsoft Excel.
     *
     * @return CSV-Datei als Download
     */
    @GetMapping("/reports/csv")
    @Operation(
            summary = "Research Reports als CSV exportieren",
            description = "Exportiert alle Research Reports als CSV-Datei mit Semikolon-Trenner "
                    + "(Schweizer Standard). UTF-8 mit BOM für Excel-Kompatibilität."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV-Datei erfolgreich erstellt"),
            @ApiResponse(responseCode = "500", description = "Fehler beim Erstellen der CSV-Datei")
    })
    public ResponseEntity<byte[]> exportReportsCsv() {
        List<ReportDto> reports = getReports.getAllReports().stream()
                .map(reportMapper::toDto)
                .toList();

        byte[] csvBytes = exportService.exportReportsCsv(reports);
        String filename = buildFilename("research-reports", "csv");

        log.info("CSV-Export gestartet: {} Reports exportiert", reports.size());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .body(csvBytes);
    }

    /**
     * Exportiert alle Research Reports als Excel-Datei (XLSX).
     *
     * <p>Das Excel enthält formatierten Headers (blauer Hintergrund, fett),
     * Auto-Width für alle Spalten, Zahlenformatierung für Finanzwerte
     * und einen Auto-Filter auf der Header-Zeile.
     *
     * @return XLSX-Datei als Download
     */
    @GetMapping("/reports/excel")
    @Operation(
            summary = "Research Reports als Excel exportieren",
            description = "Exportiert alle Research Reports als XLSX-Datei. "
                    + "Enthält formatierte Tabelle mit Header-Styling, Auto-Width und Zahlenformatierung."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Excel-Datei erfolgreich erstellt"),
            @ApiResponse(responseCode = "500", description = "Fehler beim Erstellen der Excel-Datei")
    })
    public ResponseEntity<byte[]> exportReportsExcel() {
        List<ReportDto> reports = getReports.getAllReports().stream()
                .map(reportMapper::toDto)
                .toList();

        try {
            byte[] excelBytes = exportService.exportReportsExcel(reports);
            String filename = buildFilename("research-reports", "xlsx");

            log.info("Excel-Export abgeschlossen: {} Reports exportiert", reports.size());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .body(excelBytes);

        } catch (IOException e) {
            log.error("Fehler beim Excel-Export", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String buildFilename(String base, String extension) {
        String date = LocalDate.now().format(FILE_DATE_FORMAT);
        return base + "-" + date + "." + extension;
    }
}
