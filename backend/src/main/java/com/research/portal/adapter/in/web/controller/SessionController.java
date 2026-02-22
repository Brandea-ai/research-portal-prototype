package com.research.portal.adapter.in.web.controller;

import com.research.portal.config.SessionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST-Controller für das Session-Management.
 *
 * <p>Stellt Endpunkte bereit um den Session-Status abzufragen
 * und die Session aktiv zu halten (Keep-Alive).
 *
 * <p>Endpunkte:
 * <ul>
 *   <li>{@code GET  /api/session/status}    — Session-Status abfragen</li>
 *   <li>{@code POST /api/session/keepalive} — Session verlängern (Touch)</li>
 * </ul>
 *
 * <p>FINMA-Kontext: Session-Management ist Teil der technischen Zugangskontrolle
 * für regulierte Finanzanwendungen. Inaktive Sessions müssen automatisch
 * beendet werden (FINMA-Rundschreiben 2023/1, Anhang 3).
 */
@RestController
@RequestMapping("/api/session")
@Tag(name = "Session", description = "Session-Management: Status abfragen und Keep-Alive")
public class SessionController {

    private static final Logger log = LoggerFactory.getLogger(SessionController.class);

    private final SessionConfig sessionConfig;

    public SessionController(SessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    /**
     * Gibt den aktuellen Session-Status zurück.
     *
     * <p>Liefert Informationen darüber ob eine aktive Session existiert,
     * wann sie abläuft und was das konfigurierte Inaktivitäts-Intervall ist.
     *
     * @param request HTTP-Anfrage (für Session-Zugriff)
     * @return Session-Status mit active, expiresInSeconds und maxInactiveInterval
     */
    @GetMapping("/status")
    @Operation(
            summary = "Session-Status abrufen",
            description = "Gibt den aktuellen Status der HTTP-Session zurück. "
                    + "Enthält Informationen ob die Session aktiv ist, wann sie abläuft "
                    + "und den konfigurierten maximalen Inaktivitätszeitraum."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Session-Status erfolgreich ermittelt",
            content = @Content(schema = @Schema(implementation = SessionStatusResponse.class))
    )
    public ResponseEntity<SessionStatusResponse> getSessionStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            log.debug("Session-Status abgefragt: keine aktive Session");
            return ResponseEntity.ok(new SessionStatusResponse(false, 0, 0));
        }

        long lastAccessed = session.getLastAccessedTime();
        int maxInactive = session.getMaxInactiveInterval(); // in Sekunden
        long now = System.currentTimeMillis();

        long elapsedSeconds = (now - lastAccessed) / 1000L;
        long remainingSeconds = Math.max(0L, maxInactive - elapsedSeconds);

        log.debug("Session-Status: aktiv, läuft in {}s ab (maxInactive={}s)",
                remainingSeconds, maxInactive);

        return ResponseEntity.ok(
                new SessionStatusResponse(true, remainingSeconds, maxInactive)
        );
    }

    /**
     * Verlängert die aktuelle Session (Keep-Alive / Touch).
     *
     * <p>Erstellt eine neue Session wenn keine vorhanden ist,
     * oder aktualisiert den Zeitstempel der bestehenden Session.
     *
     * @param request HTTP-Anfrage (für Session-Zugriff)
     * @return Bestätigung der Session-Verlängerung
     */
    @PostMapping("/keepalive")
    @Operation(
            summary = "Session verlängern",
            description = "Verlängert die aktuelle Session durch Aktualisierung des "
                    + "Zugriffszeitstempels (Touch). Erstellt eine neue Session wenn keine "
                    + "vorhanden ist. Soll vom Frontend periodisch aufgerufen werden "
                    + "solange der Benutzer aktiv ist."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Session erfolgreich verlängert"
    )
    public ResponseEntity<Map<String, Object>> keepAlive(HttpServletRequest request) {
        HttpSession session = request.getSession(true); // true = neue Session anlegen wenn nötig
        int maxInactive = session.getMaxInactiveInterval();

        log.debug("Session Keep-Alive: SessionId={}, maxInactive={}s",
                session.getId(), maxInactive);

        return ResponseEntity.ok(Map.of(
                "extended", true,
                "maxInactiveInterval", maxInactive,
                "sessionId", session.getId()
        ));
    }

    /**
     * DTO für die Session-Status-Antwort.
     *
     * @param active              true wenn eine aktive Session existiert
     * @param expiresInSeconds    verbleibende Sekunden bis zur Session-Ablauf
     * @param maxInactiveInterval maximaler Inaktivitätszeitraum in Sekunden
     */
    @Schema(description = "Session-Status Antwort")
    public record SessionStatusResponse(
            @Schema(description = "Gibt an ob eine aktive Session existiert", example = "true")
            boolean active,

            @Schema(description = "Verbleibende Sekunden bis zum Session-Timeout", example = "1742")
            long expiresInSeconds,

            @Schema(description = "Maximaler Inaktivitätszeitraum in Sekunden (konfiguriert)", example = "1800")
            int maxInactiveInterval
    ) {}
}
