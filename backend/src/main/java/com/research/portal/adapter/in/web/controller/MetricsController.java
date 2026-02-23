package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.ApiMetricsDto;
import com.research.portal.config.ApiMetricsCollector;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller fuer API-Nutzungsmetriken und Performance-Statistiken.
 *
 * <p>Liefert aggregierte Metriken zu allen verarbeiteten API-Anfragen:
 * Gesamtzaehler, Fehlerzaehler, durchschnittliche Antwortzeit,
 * Aufschluesselungen nach Endpunkt und Statuscode sowie Uptime.
 *
 * <p>Demonstriert Observability-Kompetenz: Self-Monitoring der API
 * als Grundlage fuer produktionsreifes Application Performance Monitoring.
 *
 * Endpunkte:
 *   GET    /api/metrics — Aktuelle API-Metriken abrufen
 *   DELETE /api/metrics — Metriken zuruecksetzen
 */
@RestController
@RequestMapping("/api/metrics")
@Tag(name = "Metrics", description = "API-Nutzungsmetriken und Performance-Statistiken")
public class MetricsController {

    private final ApiMetricsCollector metricsCollector;

    public MetricsController(ApiMetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @GetMapping
    @Operation(
            summary = "API-Metriken abrufen",
            description = "Liefert eine Momentaufnahme aller gesammelten API-Nutzungsmetriken: "
                    + "Gesamtanzahl Anfragen, Fehleranzahl, durchschnittliche Antwortzeit, "
                    + "Aufschluesselung nach Endpunkt und HTTP-Statuscode sowie die Uptime."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Metriken erfolgreich geladen",
                    content = @Content(schema = @Schema(implementation = ApiMetricsDto.class))
            )
    })
    public ResponseEntity<ApiMetricsDto> getMetrics() {
        return ResponseEntity.ok(metricsCollector.getMetrics());
    }

    @DeleteMapping
    @Operation(
            summary = "Metriken zuruecksetzen",
            description = "Setzt alle API-Metriken auf Null zurueck. "
                    + "Nuetzlich fuer Performance-Tests oder nach Deployments."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Metriken erfolgreich zurueckgesetzt"
            )
    })
    public ResponseEntity<Void> resetMetrics() {
        metricsCollector.reset();
        return ResponseEntity.noContent().build();
    }
}
