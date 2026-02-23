package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.DataValidationResultDto;
import com.research.portal.application.service.DataValidationResult;
import com.research.portal.application.service.ScheduledTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller fuer Datenintegritaetspruefung und System-Wartung.
 *
 * <p>Ermoeglicht das Abrufen des letzten Validierungsergebnisses
 * sowie das manuelle Ausloesen einer Datenvalidierung.
 * Unterstuetzt proaktive Datenqualitaetssicherung im Banking-Umfeld.
 *
 * Endpunkte:
 *   GET  /api/validation     — Letztes Validierungsergebnis abrufen
 *   POST /api/validation/run — Datenvalidierung manuell starten
 */
@RestController
@RequestMapping("/api/validation")
@Tag(name = "Data Validation", description = "Datenintegritaetspruefung und System-Wartung")
public class ValidationController {

    private static final long SIX_HOURS_MS = 6L * 60 * 60 * 1000;

    private final ScheduledTaskService scheduledTaskService;

    /**
     * Constructor Injection des ScheduledTaskService.
     *
     * @param scheduledTaskService Service fuer Datenvalidierung
     */
    public ValidationController(ScheduledTaskService scheduledTaskService) {
        this.scheduledTaskService = scheduledTaskService;
    }

    /**
     * Gibt das Ergebnis der letzten Datenvalidierung zurueck.
     * Falls noch keine Validierung gelaufen ist, werden Standardwerte zurueckgegeben.
     *
     * @return DTO mit Validierungsergebnis
     */
    @GetMapping
    @Operation(
            summary = "Letztes Validierungsergebnis abrufen",
            description = "Gibt das Ergebnis der letzten planmaessigen oder manuellen "
                    + "Datenvalidierung zurueck, inklusive Zeitstempel und geschaetztem naechsten Lauf."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Validierungsergebnis erfolgreich geladen",
                    content = @Content(schema = @Schema(implementation = DataValidationResultDto.class))
            )
    })
    public ResponseEntity<DataValidationResultDto> getLastResult() {
        DataValidationResult result = scheduledTaskService.getLastValidationResult();
        LocalDateTime lastRun = scheduledTaskService.getLastValidationRun();

        if (result == null) {
            DataValidationResultDto dto = new DataValidationResultDto(
                    0, 0, 0, 0, 0, false,
                    "Noch kein Lauf durchgefuehrt",
                    "Wird beim naechsten Schedule-Intervall ausgefuehrt"
            );
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.ok(toDto(result, lastRun));
    }

    /**
     * Startet eine Datenvalidierung manuell und gibt das Ergebnis zurueck.
     *
     * @return DTO mit dem Ergebnis der soeben durchgefuehrten Validierung
     */
    @PostMapping("/run")
    @Operation(
            summary = "Datenvalidierung manuell starten",
            description = "Fuehrt eine sofortige Datenvalidierung durch, unabhaengig vom "
                    + "automatischen 6-Stunden-Intervall. Das Ergebnis wird gespeichert und zurueckgegeben."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Validierung erfolgreich durchgefuehrt",
                    content = @Content(schema = @Schema(implementation = DataValidationResultDto.class))
            )
    })
    public ResponseEntity<DataValidationResultDto> runValidation() {
        DataValidationResult result = scheduledTaskService.runValidation();
        LocalDateTime now = LocalDateTime.now();

        return ResponseEntity.ok(toDto(result, now));
    }

    /**
     * Konvertiert ein DataValidationResult in ein DTO.
     */
    private DataValidationResultDto toDto(DataValidationResult result, LocalDateTime lastRun) {
        String lastRunAt = lastRun != null
                ? lastRun.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "Unbekannt";

        String nextRunAt = lastRun != null
                ? lastRun.plusHours(6).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "Unbekannt";

        return new DataValidationResultDto(
                result.getOrphanedReports(),
                result.getInvalidSecurityRefs(),
                result.getNegativeMarketCaps(),
                result.getInvalidAccuracies(),
                result.totalIssues(),
                result.hasIssues(),
                lastRunAt,
                nextRunAt
        );
    }
}
