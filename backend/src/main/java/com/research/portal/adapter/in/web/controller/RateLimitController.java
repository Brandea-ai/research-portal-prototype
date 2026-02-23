package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.RateLimitStatsDto;
import com.research.portal.adapter.in.web.dto.RateLimitStatusDto;
import com.research.portal.application.service.RateLimitService;
import com.research.portal.application.service.RateLimitService.RateLimitInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller fuer API Rate-Limit-Verwaltung und Monitoring.
 *
 * <p>Bietet Endpunkte zur Abfrage des aktuellen Rate-Limit-Status,
 * zum Zuruecksetzen aller Limits (Admin-Operation) und zum Abrufen
 * von Rate-Limiting-Statistiken.
 *
 * Endpunkte:
 *   GET    /api/rate-limit/status — Aktueller Rate-Limit-Status des Clients
 *   DELETE /api/rate-limit/reset  — Alle Rate-Limits zuruecksetzen
 *   GET    /api/rate-limit/stats  — Rate-Limiting-Statistiken
 */
@RestController
@RequestMapping("/api/rate-limit")
@Tag(name = "Rate Limiting", description = "API Rate-Limit-Verwaltung und Monitoring")
public class RateLimitController {

    private final RateLimitService rateLimitService;

    /**
     * Erstellt einen neuen RateLimitController.
     *
     * @param rateLimitService der injizierte RateLimitService
     */
    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    /**
     * Gibt den aktuellen Rate-Limit-Status fuer den aufrufenden Client zurueck.
     * Zeigt fuer die Kategorie READ die verbleibenden Requests, das Limit
     * und den Reset-Zeitpunkt an.
     *
     * @param request der HTTP-Request (fuer IP-Extraktion)
     * @return Rate-Limit-Status des Clients
     */
    @GetMapping("/status")
    @Operation(
            summary = "Rate-Limit-Status abfragen",
            description = "Gibt den aktuellen Rate-Limit-Status fuer den aufrufenden Client zurueck. "
                    + "Zeigt verbleibende Requests, Limit und Reset-Zeitpunkt fuer die Kategorie READ."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Rate-Limit-Status erfolgreich abgerufen",
                    content = @Content(schema = @Schema(implementation = RateLimitStatusDto.class))
            )
    })
    public ResponseEntity<RateLimitStatusDto> getStatus(HttpServletRequest request) {
        String clientIp = extractClientIp(request);
        String category = RateLimitService.CATEGORY_READ;

        RateLimitInfo info = rateLimitService.getRateLimitInfo(clientIp, category);
        boolean isLimited = info.remaining() <= 0;

        RateLimitStatusDto dto = new RateLimitStatusDto(
                clientIp, category, info.remaining(), info.limit(),
                info.resetAtEpochMs(), isLimited
        );

        return ResponseEntity.ok(dto);
    }

    /**
     * Setzt alle Rate-Limits zurueck.
     * Admin-Operation zum Freigeben aller blockierten Clients.
     *
     * @return 200 OK mit Bestaetigungsnachricht
     */
    @DeleteMapping("/reset")
    @Operation(
            summary = "Rate-Limits zuruecksetzen",
            description = "Setzt alle Rate-Limits zurueck. Admin-Operation zum Freigeben "
                    + "aller blockierten Clients."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Rate-Limits erfolgreich zurueckgesetzt"
            )
    })
    public ResponseEntity<Map<String, String>> reset() {
        rateLimitService.resetAll();
        return ResponseEntity.ok(Map.of("message", "Alle Rate-Limits zurueckgesetzt"));
    }

    /**
     * Gibt Statistiken ueber das Rate-Limiting zurueck.
     * Zeigt die Gesamtanzahl blockierter Requests, aktive Clients
     * und Blockierungen pro Kategorie.
     *
     * @return Rate-Limiting-Statistiken
     */
    @GetMapping("/stats")
    @Operation(
            summary = "Rate-Limit-Statistiken abrufen",
            description = "Gibt Statistiken ueber das Rate-Limiting zurueck: "
                    + "Gesamtanzahl blockierter Requests, aktive Clients "
                    + "und Blockierungen pro Kategorie."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistiken erfolgreich abgerufen",
                    content = @Content(schema = @Schema(implementation = RateLimitStatsDto.class))
            )
    })
    public ResponseEntity<RateLimitStatsDto> getStats() {
        Map<String, Object> stats = rateLimitService.getStats();

        @SuppressWarnings("unchecked")
        Map<String, Integer> blockedByCategory =
                (Map<String, Integer>) stats.get("blockedByCategory");

        RateLimitStatsDto dto = new RateLimitStatsDto(
                (long) stats.get("totalBlocked"),
                (int) stats.get("activeClients"),
                blockedByCategory
        );

        return ResponseEntity.ok(dto);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
