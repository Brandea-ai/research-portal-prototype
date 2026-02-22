package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.CreateReportRequest;
import com.research.portal.adapter.in.web.dto.ErrorResponse;
import com.research.portal.adapter.in.web.dto.ReportDto;
import com.research.portal.adapter.in.web.mapper.ReportApiMapper;
import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.port.in.GetReportsUseCase;
import com.research.portal.domain.port.in.ManageReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Research Report Verwaltung")
public class ReportController {

    private final GetReportsUseCase getReports;
    private final ManageReportUseCase manageReport;
    private final ReportApiMapper mapper;

    public ReportController(GetReportsUseCase getReports,
                            ManageReportUseCase manageReport,
                            ReportApiMapper mapper) {
        this.getReports = getReports;
        this.manageReport = manageReport;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(
            summary = "Alle Research Reports abrufen",
            description = "Liefert eine Liste aller publizierten Research Reports. "
                    + "Unterstützt optionale Filterung nach Analyst oder Wertschrift über Query-Parameter."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste der Reports erfolgreich geladen",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReportDto.class)))
    )
    public List<ReportDto> getAllReports() {
        return getReports.getAllReports().stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Report nach ID abrufen",
            description = "Liefert einen einzelnen Research Report anhand seiner eindeutigen ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Report gefunden",
                    content = @Content(schema = @Schema(implementation = ReportDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report nicht gefunden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ReportDto> getReportById(
            @Parameter(description = "Eindeutige Report-ID", example = "1")
            @PathVariable Long id) {
        return getReports.getReportById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Report", id));
    }

    @GetMapping(params = "analystId")
    @Operation(
            summary = "Reports nach Analyst filtern",
            description = "Liefert alle Research Reports eines bestimmten Analysten."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Reports des Analysten erfolgreich geladen",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReportDto.class)))
    )
    public List<ReportDto> getReportsByAnalyst(
            @Parameter(description = "ID des Analysten", example = "1")
            @RequestParam Long analystId) {
        return getReports.getReportsByAnalyst(analystId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping(params = "securityId")
    @Operation(
            summary = "Reports nach Wertschrift filtern",
            description = "Liefert alle Research Reports zu einer bestimmten Wertschrift (Security)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Reports zur Wertschrift erfolgreich geladen",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReportDto.class)))
    )
    public List<ReportDto> getReportsBySecurity(
            @Parameter(description = "ID der Wertschrift", example = "1")
            @RequestParam Long securityId) {
        return getReports.getReportsBySecurity(securityId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @PostMapping
    @Operation(
            summary = "Neuen Research Report erstellen",
            description = "Erstellt einen neuen Research Report mit Rating, Kursziel und Analyse. "
                    + "Analyst und Wertschrift müssen bereits im System existieren."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Report erfolgreich erstellt",
                    content = @Content(schema = @Schema(implementation = ReportDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ungültige Eingabedaten",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ReportDto> createReport(
            @Valid @RequestBody CreateReportRequest request) {
        var domain = mapper.toDomain(request);
        var saved = manageReport.createReport(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Research Report aktualisieren",
            description = "Aktualisiert einen bestehenden Research Report. "
                    + "Kann für Rating-Änderungen, Kursziel-Anpassungen oder inhaltliche Updates verwendet werden."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Report erfolgreich aktualisiert",
                    content = @Content(schema = @Schema(implementation = ReportDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report nicht gefunden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ungültige Eingabedaten",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ReportDto updateReport(
            @Parameter(description = "Eindeutige Report-ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CreateReportRequest request) {
        var domain = mapper.toDomain(request);
        var updated = manageReport.updateReport(id, domain);
        return mapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Research Report löschen",
            description = "Löscht einen Research Report unwiderruflich aus dem System."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Report erfolgreich gelöscht"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report nicht gefunden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "Eindeutige Report-ID", example = "1")
            @PathVariable Long id) {
        manageReport.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
