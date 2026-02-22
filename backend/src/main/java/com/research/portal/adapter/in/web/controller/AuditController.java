package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.AuditLogDto;
import com.research.portal.adapter.in.web.mapper.AuditLogApiMapper;
import com.research.portal.domain.port.in.AuditLogUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für den Audit-Trail (Activity Log).
 *
 * Stellt Endpunkte bereit, um den Audit-Trail abzufragen.
 * Dies ist ein zentraler Bestandteil der FINMA-Compliance:
 * Alle Aktionen im System müssen jederzeit nachvollziehbar sein.
 */
@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit Trail", description = "FINMA-konformer Audit-Trail: Protokollierung aller Benutzeraktionen")
public class AuditController {

    private final AuditLogUseCase auditLogUseCase;
    private final AuditLogApiMapper mapper;

    public AuditController(AuditLogUseCase auditLogUseCase, AuditLogApiMapper mapper) {
        this.auditLogUseCase = auditLogUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(
            summary = "Audit-Trail abrufen",
            description = "Gibt die letzten Audit-Einträge zurück. "
                    + "Kann optional nach Entitätstyp und ID gefiltert werden. "
                    + "Standardmässig werden die letzten 50 Einträge zurückgegeben."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Audit-Einträge erfolgreich geladen",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogDto.class)))
    )
    public List<AuditLogDto> getAuditLogs(
            @Parameter(description = "Entitätstyp zum Filtern (z.B. REPORT, SECURITY, ANALYST)")
            @RequestParam(required = false) String entityType,
            @Parameter(description = "Entitäts-ID zum Filtern")
            @RequestParam(required = false) Long entityId,
            @Parameter(description = "Maximale Anzahl der zurückgegebenen Einträge", example = "50")
            @RequestParam(defaultValue = "50") int limit) {

        if (entityType != null && entityId != null) {
            return auditLogUseCase.getLogsByEntity(entityType, entityId).stream()
                    .map(mapper::toDto)
                    .toList();
        }

        return auditLogUseCase.getRecentLogs(limit).stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping("/report/{id}")
    @Operation(
            summary = "Audit-Trail für einen Report",
            description = "Gibt alle Audit-Einträge für einen bestimmten Research Report zurück. "
                    + "Zeigt die vollständige Änderungshistorie: Erstellung, Updates, Views."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Audit-Trail für den Report erfolgreich geladen",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditLogDto.class)))
    )
    public List<AuditLogDto> getAuditTrailForReport(
            @Parameter(description = "ID des Research Reports", example = "1")
            @PathVariable Long id) {
        return auditLogUseCase.getLogsByEntity("REPORT", id).stream()
                .map(mapper::toDto)
                .toList();
    }
}
